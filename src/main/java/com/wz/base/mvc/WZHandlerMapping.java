package com.wz.base.mvc;

import com.wz.base.annotation.WZRequestMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.security.PublicKey;
import java.util.regex.Pattern;
@Data
public class WZHandlerMapping {
    private Object controller; // 目标方法所在的Controller
    private Method method; // URL 对应的方法
    private Pattern pattern; // URL 的封装

    public WZHandlerMapping() {

    }

    public WZHandlerMapping(Object controller, Method method, Pattern pattern) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }
}
