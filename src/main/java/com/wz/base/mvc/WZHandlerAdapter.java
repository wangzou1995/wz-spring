package com.wz.base.mvc;

import com.wz.base.annotation.WZRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 将请求传递到服务端的参数列表与Method实参列表的对应关系，完成参数值的类型转换工作。
 */
public class WZHandlerAdapter {

    public boolean supports(Object handler) {
        return (handler instanceof WZHandlerMapping);
    }

    public Object handle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws InvocationTargetException, IllegalAccessException {
        WZHandlerMapping handlerMapping = (WZHandlerMapping) handler;
        // 存放每个方法的参数列表
        Map<String, Integer> paramMapping = new HashMap<String, Integer>(8);

        Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();

        for (int i = 0; i < pa.length; i++) {
            for (Annotation a : pa[i]) {
                if (a instanceof WZRequestParam) {
                    String paramName = ((WZRequestParam) a).value();
                    if (!"".equals(paramName.trim())) {
                        paramMapping.put(paramName, i);
                    }
                }
            }
        }

        Class<?> [] paramsTypes = handlerMapping.getMethod().getParameterTypes();
        for (int i = 0; i <paramsTypes.length ; i++) {
           Object type = paramsTypes[i];
            if (type == HttpServletRequest.class || type == HttpServletResponse.class) {
                paramMapping.put(type.getClass().getName(),i);
            }
        }

        Map<String ,String[]> reqParameterMap = req.getParameterMap();

        Object [] paramValues = new Object[paramsTypes.length];
        for(Map.Entry<String, String[]> param: reqParameterMap.entrySet()){
            String value = Arrays.toString(param.getValue()).replaceAll("[\\[\\]]","").replaceAll("\\s","");
            if (!paramMapping.containsKey(param.getKey())){continue;}
            int index = paramMapping.get(param.getKey());

            paramValues[index] = caseStringValue(value,paramsTypes[index]);
        }

        if(paramMapping.containsKey(HttpServletRequest.class.getName())){
            int reqIndex = paramMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
        }

        if(paramMapping.containsKey(HttpServletResponse.class.getName())){
            int respIndex = paramMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = resp;
        }
        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(),paramValues);
        if (null == result) {
            return  null;
        }
        boolean isModelView = handlerMapping.getMethod().getReturnType() == WZModelAndView.class;
        if(isModelView) {
            return result;
        } else {
            return result;
        }

    }

    private Object caseStringValue(String value, Class<?> clazz) {
        if (clazz == String.class) {
            return value;
        } else if (clazz == Integer.class) {
            return Integer.valueOf(value);
        } else if (clazz == int.class) {
            return Integer.valueOf(value);
        } else {
            return null;
        }
    }
}
