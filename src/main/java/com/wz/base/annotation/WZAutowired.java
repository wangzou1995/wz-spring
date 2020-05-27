package com.wz.base.annotation;

import java.lang.annotation.*;

/**
 * 自动注入
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WZAutowired {
    String value() default "";
}
