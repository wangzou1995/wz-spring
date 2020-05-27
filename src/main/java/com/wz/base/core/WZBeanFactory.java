package com.wz.base.core;

public interface WZBeanFactory {

    /**
     * 根据BeanName从Ioc容器获取Bean实例
     *
     * @param beanName bean名称
     * @return Bean实例
     * @throws Exception 异常
     */
    Object getBean(String beanName) throws Exception;

    public Object getBean(Class<?> beanClass) throws Exception;
}
