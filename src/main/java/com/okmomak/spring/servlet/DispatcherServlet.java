package com.okmomak.spring.servlet;

import com.okmomak.spring.Handler;
import com.okmomak.spring.annotation.Controller;
import com.okmomak.spring.annotation.RequestMapping;
import com.okmomak.spring.annotation.RequestParam;
import com.okmomak.spring.context.WebApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DispatcherServlet extends HttpServlet {
    private List<Handler> handlerList = new ArrayList<>();
    Map<String, Object> defaultParams = new HashMap<>();
    WebApplicationContext webApplicationContext;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        String contextConfigLocation = servletConfig.getInitParameter("contextConfigLocation");
        webApplicationContext = new WebApplicationContext();
        webApplicationContext.init(contextConfigLocation);
        initHandler();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        defaultParams.put("HttpServletRequest", req);
        defaultParams.put("HttpServletResponse", resp);
        executeDispatch(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private void initHandler() {
        ConcurrentHashMap<String, Object> singletonObjects = webApplicationContext.singletonObjects;
        if (singletonObjects.isEmpty()) {
            return;
        }

        for (String beanName : singletonObjects.keySet()) {
            Object obj = singletonObjects.get(beanName);
            Class<?> clazz = obj.getClass();
            if (!clazz.isAnnotationPresent(Controller.class)) {
                continue;
            }

            // String value = "";
            for (Method method : clazz.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(RequestMapping.class)) {
                    continue;
                }

                String value = method.getAnnotation(RequestMapping.class).value();
                handlerList.add(new Handler(value, obj, method));
            }
        }
    }

    private void executeDispatch(HttpServletRequest request, HttpServletResponse response) {
        Handler handler = getHandler(request, response);
        try {
            if (handler == null) {
                response.getWriter().print("<h1>404 NOT FOUND</h1>");
            } else {
                Method method = handler.getMethod();
                Object[] params = getParams(method, request);

                for (Object param : params) {
                    System.out.println("param = " + param);
                }
                method.invoke(handler.getController(), params);
            }
        } catch (IOException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private Object[] getParams(Method method, HttpServletRequest request) {
        // 获得方法中的所有参数的名字

        Parameter[] parameters = method.getParameters();

        Map<String, String[]> parameterMap = request.getParameterMap();
        Object[] params = packageParameters(parameters, parameterMap);
        return params;
    }

    private Object[] packageParameters(Parameter[] parameters, Map<String, String[]> parameterMap) {
        int len = parameters.length;
        Object[] params = new Object[len];
        for (int i = 0; i < len; i++) {
            Parameter parameter = parameters[i];
            if (defaultParams.containsKey(parameter.getType().getSimpleName())) {
                params[i] = defaultParams.get(parameter.getType().getSimpleName());
            } else if (parameter.isAnnotationPresent(RequestParam.class)) {
                String paraName = parameter.getAnnotation(RequestParam.class).value();
                if (paraName.isEmpty()) {
                    paraName = parameter.getName();
                }

                String[] paraValues = parameterMap.get(paraName);
                if (paraValues != null) {
                    params[i] = paraValues[0];
                }
            } else {
                String name = parameter.getName();
                String[] value = parameterMap.get(name);
                if (value != null) {
                    params[i] = value[0];
                }
            }
        }
        return params;
    }

    private void packageNoAnnotationParameters(Object[] params, Parameter[] parameters, Map<String, String[]> parameterMap) {
        for (int i = 0; i < parameters.length; i++) {
            if (params[i] != null) {
                continue;
            }

            Parameter parameter = parameters[i];
            String name = parameter.getName();
            String[] value = parameterMap.get(name);
            if (value == null) {
                continue;
            }
            params[i] = value[0];
        }
    }

    private void packageAnnotationParameters(Object[] params, Parameter[] parameters, Map<String, String[]> parameterMap) {
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            System.out.println("parameter.getName() = " + parameter.getName());
            if (parameter.isAnnotationPresent(RequestParam.class)) {
                String paraName = parameter.getAnnotation(RequestParam.class).value();
                if (!parameterMap.containsKey(paraName)) {
                    continue;
                }

                if (paraName.isEmpty()) {
                    paraName = parameter.getName();
                }
                String[] paraValues = parameterMap.get(paraName);
                if (paraValues != null) {
                    params[i] = paraValues[0];
                }
            }
        }
    }

    private Object[] packageDefaultParameters(Class<?>[] parameterTypes) {

        int len = parameterTypes.length;
        Object[] params = new Object[len];
        // 封装 HttpServletRequest, HttpServletResponse 到参数数组
        for (int i = 0; i < len; i++) {
            for (String key : defaultParams.keySet()) {
                if (key.equals(parameterTypes[i].getSimpleName())) {
                    params[i] = defaultParams.get(key);
                }
            }
        }

        return params;
    }

    private Handler getHandler(HttpServletRequest request, HttpServletResponse response) {
        String requestURI = request.getRequestURI();
        requestURI = requestURI.substring(request.getContextPath().length());
        for (Handler handler : handlerList) {
            String url = handler.getUrl();
            if (requestURI.equals(url) || requestURI.equals("/" + url)) {
                return handler;
            }
        }

        return null;
    }
}
