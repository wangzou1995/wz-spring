package com.wz.base.annotation;

import java.lang.annotation.*;

/**
 * 页面交互
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WZController {
    String value() default "";
}
