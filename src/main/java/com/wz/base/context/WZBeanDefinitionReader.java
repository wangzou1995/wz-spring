package com.wz.base.context;

import com.wz.base.beans.WZBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 对配置文件进行查找、读取、解析
 */
public class WZBeanDefinitionReader {
    private List<String> registryBeanClasses = new ArrayList<String>();
    private Properties config = new Properties();
    private final String SCAN_PACKAGE = "scanPackage";

    public WZBeanDefinitionReader() {

    }

    public WZBeanDefinitionReader(String... locations) {
        // 通过URL定位找到相应的文件
        InputStream inputStream = this.getClass().getClassLoader()
                .getResourceAsStream(locations[0].replace("classpath:", ""));
        try {
            config.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource(scanPackage.replaceAll("\\.", "/"));
        File classFile = new File(url.getFile());
        for (File file : classFile.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String className = (scanPackage + "." + file.getName().replace(".class", ""));
                registryBeanClasses.add(className);
            }
        }
    }

    public List<WZBeanDefinition> loadBeanDefinitions() {
        List<WZBeanDefinition> result = new ArrayList<WZBeanDefinition>();
        try {
            for (String className : registryBeanClasses) {
                Class<?> beanClass = Class.forName(className);
                // 排除接口和抽象类
                if (beanClass.isInterface() || Modifier.isAbstract(beanClass.getModifiers())) {
                    continue;
                }
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));
                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> i : interfaces) {
                    result.add(doCreateBeanDefinition(i.getName(), beanClass.getName()));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 创建BeanDefinition
     *
     * @param factoryBeanName
     * @param beanClassName
     * @return
     */
    private WZBeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName) {
        WZBeanDefinition beanDefinition = new WZBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;

    }

    /**
     * 首字母小写
     *
     * @param simpleName
     * @return
     */
    public static String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    public Properties getConfig() {
        return this.config;
    }
}
