package com.wz.base.context;

import com.wz.base.beans.WZBeanPostProcessor;
import com.wz.base.annotation.WZAutowired;
import com.wz.base.annotation.WZController;
import com.wz.base.annotation.WZService;
import com.wz.base.beans.WZBeanDefinition;
import com.wz.base.beans.WZBeanWrapper;
import com.wz.base.core.WZBeanFactory;
import com.wz.base.core.WZDefaultListableBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户入口
 */
public class WZApplicationContext extends WZDefaultListableBeanFactory implements WZBeanFactory {

    private String[] configLocations;
    private WZBeanDefinitionReader reader;
    // 单列的Ioc容器缓存
    private Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<String, Object>();
    // 通用的Ioc容器
    private Map<String, WZBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, WZBeanWrapper>();

    WZApplicationContext(){
    }
    public WZApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() throws Exception {
        // 1.定位配置文件
        reader = new WZBeanDefinitionReader(configLocations);
        // 2.记载配置文件，扫描相关的类，把他们封装成BeanDefinition
        List<WZBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        // 3.注册，把配置信息注册到容器里面
        doRegisterBeanDefinition(beanDefinitions);
        // 4.把不是延迟加载的类进行初始化
        doAutowired();
    }

    private void doAutowired() {
        for (Map.Entry<String, WZBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegisterBeanDefinition(List<WZBeanDefinition> beanDefinitions) throws Exception {
        for (WZBeanDefinition beanDefinition : beanDefinitions) {
            if (super.beanDefinitionMap.containsKey(beanDefinition.getBeanClassName())) {
                throw new Exception("The " + beanDefinition.getFactoryBeanName() + "is exists");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
    }

    public Object getBean(String beanName) throws Exception {
        WZBeanDefinition beanDefinition = super.beanDefinitionMap.get(beanName);
        try {
            // 生成通知事件
            WZBeanPostProcessor beanPostProcessor = new WZBeanPostProcessor();
            Object instance = instantiateBean(beanDefinition);

            if (null == instance) {
                return null;
            }
            // 在实例化初始前调用一次
            beanPostProcessor.postPostProcessBeforeInitialization(instance, beanName);

            WZBeanWrapper beanWrapper = new WZBeanWrapper(instance);
            this.factoryBeanInstanceCache.put(beanName, beanWrapper);
            // 在实例化以后调用一次
            beanPostProcessor.postPostProcessAfterInitialization(instance, beanName);
            // 填充 ...
            populateBean(beanName, instance);
            return this.factoryBeanInstanceCache.get(beanName).getWrapperInstance();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void populateBean(String beanName, Object instance) {
        Class clazz = instance.getClass();
        if (!(clazz.isAnnotationPresent(WZController.class) || clazz.isAnnotationPresent(WZService.class))) {
            return;
        };
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(WZAutowired.class)) {
                continue;
            }
            WZAutowired autowired = field.getAnnotation(WZAutowired.class);
            String autowiredName = autowired.value().trim();
            if ("".equals(autowiredName)) {
                autowiredName = field.getType().getName();
            }
            field.setAccessible(true);
            try {
                field.set(instance, this.factoryBeanInstanceCache.get(autowiredName).getWrapperInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private Object instantiateBean(WZBeanDefinition beanDefinition) {
        Object instance = null;
        String className = beanDefinition.getBeanClassName();
        try {
            if (this.factoryBeanObjectCache.containsKey(className)) {
                instance = this.factoryBeanObjectCache.get(className);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                this.factoryBeanObjectCache.put(className, instance);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return instance;
    }

    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass);
    }

    // 进行包装
    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig() {
        return this.reader.getConfig();
    }
}
