package com.wz.base.core;

import com.wz.base.context.WZAbstractApplicationContext;
import com.wz.base.beans.WZBeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Ioc容器子类
 */
public class WZDefaultListableBeanFactory extends WZAbstractApplicationContext {
    // 存储注册信息的BeanDefinition
    protected final Map<String,WZBeanDefinition> beanDefinitionMap =
            new ConcurrentHashMap<String, WZBeanDefinition>(8);
}
