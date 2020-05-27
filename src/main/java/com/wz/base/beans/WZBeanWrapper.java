package com.wz.base.beans;

/**
 * 该对象主要用于封装创建后的对象实例，代理对象，原生对象都由BeanWrapper保存
 */
public class WZBeanWrapper {
    private Object wrapperInstance;
    private Class<?> wrapperClass;

    public WZBeanWrapper(Object wrapperInstance) {
        this.wrapperInstance = wrapperInstance;
    }

    public WZBeanWrapper(){};
    public Object getWrapperInstance() {
        return wrapperInstance;
    }

    //返回代理后的class
    public Class<?> getWrapperClass() {
        return wrapperClass;
    }
}
