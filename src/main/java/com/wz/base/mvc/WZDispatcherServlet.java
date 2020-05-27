package com.wz.base.mvc;

import com.wz.base.annotation.*;
import com.wz.base.context.WZApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * mvc入口启动类
 */
//@Slf4j
public class WZDispatcherServlet extends HttpServlet {
    private final String LOCATION = "contextConfigLocation";
    /**
     * 映射链表
     */
    private List<WZHandlerMapping> handlerMappings = new ArrayList<WZHandlerMapping>();
    /**
     * 适配器链表
     */
    private Map<WZHandlerMapping, WZHandlerAdapter> handlerAdapters = new HashMap<WZHandlerMapping,WZHandlerAdapter>();
    /**
     * 试图解析器
     */
    private List<WZViewResolver> viewResolvers = new ArrayList<WZViewResolver>();

    private WZApplicationContext context;

    @Override
    public void init(ServletConfig config) throws ServletException {
        context = new WZApplicationContext(config.getInitParameter(LOCATION));
        initStrategies(context);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws InvocationTargetException, IllegalAccessException, IOException {

        WZHandlerMapping handler = getHandler(req);
        if (handler == null) {
            return;
        }
        WZHandlerAdapter adapter = getHandleAdapter(handler);

        Object modelAndView = adapter.handle(req,resp,handler);
        resp.setStatus(200);
        resp.getWriter().write(modelAndView.toString());
    }

    private WZHandlerAdapter getHandleAdapter(WZHandlerMapping handler) {
        if (this.handlerAdapters.isEmpty()) {
            return null;
        }
        WZHandlerAdapter adapter = this.handlerAdapters.get(handler);
        if (adapter.supports(handler)) {
            return adapter;
        }
        return null;
    }

    private WZHandlerMapping getHandler(HttpServletRequest req) {

        if (handlerMappings.isEmpty()) {
            return null;
        }
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replaceFirst(contextPath, "").replaceAll("/+", "/");
        for (WZHandlerMapping handlerMapping : this.handlerMappings) {
            Matcher matcher = handlerMapping.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handlerMapping;
        }
        return null;
    }

    private void initStrategies(WZApplicationContext context) {
        // 九种策略
        initMultipartResolver(context); //文件上传试图解析器
        initThemeResolver(context); // 主题解析器
        initHandlerMappings(context); // 请求映射处理器，通过HandlerMapper 将请求映射到处理器
        initHandlerAdapters(context); // 通过HandlerAdapter 多类型参数动态匹配
        initHandlerExceptionResolvers(context);
        initRequestToViewNameTranslator(context); // 将请求解析到视图名
        initViewResolvers(context); // 自己解析一套模版语言
        initFlashMapManager(context);
    }

    private void initFlashMapManager(WZApplicationContext context) {
    }

    private void initViewResolvers(WZApplicationContext context) {
    }

    private void initRequestToViewNameTranslator(WZApplicationContext context) {
    }

    private void initHandlerExceptionResolvers(WZApplicationContext context) {
    }

    private void initHandlerAdapters(WZApplicationContext context) {
        for (WZHandlerMapping handlerMapping : this.handlerMappings) {
            this.handlerAdapters.put(handlerMapping, new WZHandlerAdapter());
        }
    }

    private void initHandlerMappings(WZApplicationContext context) {
        String[] beanNames = context.getBeanDefinitionNames();
        try {
            for (String beanName : beanNames) {
                Object controller = context.getBean(beanName);
                Class<?> clazz = controller.getClass();
                if (!clazz.isAnnotationPresent(WZController.class)) {
                    continue;
                }
                String baseUrl = "";
                if (clazz.isAnnotationPresent(WZRequestMapping.class)) {
                    WZRequestMapping requestMapping = clazz.getAnnotation(WZRequestMapping.class);
                    baseUrl = requestMapping.value();
                }
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    if (!method.isAnnotationPresent(WZRequestMapping.class)) {
                        continue;
                    }
                    WZRequestMapping requestMapping = method.getAnnotation(WZRequestMapping.class);
                    String regex = ("/" + baseUrl + requestMapping.value().replace("\\*", ".*")).replaceAll("/+", "/");
                    Pattern pattern = Pattern.compile(regex);
                    this.handlerMappings.add(new WZHandlerMapping(controller, method, pattern));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initThemeResolver(WZApplicationContext context) {
    }

    private void initMultipartResolver(WZApplicationContext context) {
    }
}
