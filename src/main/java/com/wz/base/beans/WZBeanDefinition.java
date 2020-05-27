package com.wz.base.beans;

/**
 * 保存bean的配置信息
 */
public class WZBeanDefinition {
    private String beanClassName;  // 原生Bean的全类名
    private boolean lazyInit = false; // 是否延迟初始化
    private String factoryBeanName; // 保存Bean的名称， 在Ioc容器中存储的key

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }
}
