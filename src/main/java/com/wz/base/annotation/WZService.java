package com.wz.base.annotation;

import java.lang.annotation.*;

/**
 * 业务逻辑注入接口
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WZService {
    String value() default "";
}
