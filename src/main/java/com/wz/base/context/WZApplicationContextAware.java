package com.wz.base.context;

/**
 * 通过解耦方式获得Ioc容器顶层设计
 */
public interface WZApplicationContextAware {
    void setApplicationContext(WZApplicationContext applicationContext);
}
