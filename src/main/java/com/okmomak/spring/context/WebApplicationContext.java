package com.okmomak.spring.context;

import com.okmomak.spring.annotation.Autowired;
import com.okmomak.spring.annotation.Controller;
import com.okmomak.spring.annotation.Service;
import com.okmomak.spring.xml.XMLParser;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

Simple springMVc
public class WebApplicationContext {
    private List<String> classFullPathList = new ArrayList<>();
    public ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();
    private static final List<Class<? extends Annotation>> annotationClasses = new ArrayList<>();

    static {
        annotationClasses.add(Controller.class);
        annotationClasses.add(Service.class);
    }

    public void init(String contextConfigLocation) {
        getAllClassesFullPath(contextConfigLocation.split(":")[1]);
        System.out.println("classFullPathList = " + classFullPathList);
        executeInstance();

        executeAutoWired();
        System.out.println("ok");
    }

    private void executeInstance() {
        if (classFullPathList.isEmpty()) {
            return ;
        }

        for (String classPath : classFullPathList) {
            try {
                Class<?> clazz = Class.forName(classPath);
                List<String> beanNames = getBeanNames(clazz);
                boolean exist = false;
                for (String beanName : beanNames) {
                    if (getBean(beanName) != null) {
                        exist = true;
                    }
                }
                if (exist) {
                    continue;
                }

                Object instance = clazz.newInstance();;
                for (Class<? extends Annotation> annotationClass : annotationClasses) {
                    if (clazz.isAnnotationPresent(annotationClass)) {
                        for (String beanName : beanNames) {
                            singletonObjects.put(beanName, instance);
                        }
                        break;
                    }
                }
            } catch (ClassNotFoundException | InstantiationException |
                     IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private Object createBean(Class<?> clazz) {
        System.out.println("clazz = " + clazz);
        Object instance = null;

        try {
            instance = clazz.newInstance();
            injectDependencies(instance);
        } catch (InstantiationException
                 | IllegalAccessException e) {
            e.printStackTrace();
        }
        return instance;
    }

    private void executeAutoWired() {
        for (Map.Entry<String, Object> entry : singletonObjects.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            injectDependencies(value);
        }
    }

    private void injectDependencies(Object bean) {
        Class<?> clazz = bean.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Autowired.class)) {
                continue;
            }

            String value = field.getDeclaredAnnotation(Autowired.class).value();
            Object fieldBean = null;
            if (value.isEmpty()) {
                fieldBean = getBean(field.getType());
            } else {
                fieldBean = getBean(value);
            }

            if (fieldBean == null) {
                fieldBean = createBean(field.getType());
                List<String> beanNames = getBeanNames(field.getType());
                for (String beanName : beanNames) {
                    singletonObjects.put(beanName, fieldBean);
                }
            }

            try {
                field.setAccessible(true);
                field.set(bean, fieldBean);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private Object getBean(String beanName) {
        if (singletonObjects.containsKey(beanName)) {
            return singletonObjects.get(beanName);
        }
        return null;
    }

    private Object getBean(Class<?> clazz) {
        List<String> beanNames = getBeanNames(clazz);
        for (String beanName : beanNames) {
            Object bean = getBean(beanName);
            if (bean != null) {
                return bean;
            }
        }

        return null;
    }

    private List<String> getBeanNames(Class<?> clazz) {
        List<String> beanNames = new ArrayList<>();

        for (Class<? extends Annotation> annotationClass : annotationClasses) {
            Annotation annotation = clazz.getDeclaredAnnotation(annotationClass);
            String beanName = "";
            if (annotation == null) {
                continue;
            }
            try {
                beanName = (String) annotationClass.getMethod("value").invoke(annotation);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }

            if (!beanName.isEmpty()) {
                beanNames.add(beanName);
                return beanNames;
            }
        }

        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            String simpleName = anInterface.getSimpleName();
            simpleName = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
            beanNames.add(simpleName);
        }

        String className = clazz.getSimpleName();
        className = Character.toLowerCase(className.charAt(0)) + className.substring(1);
        beanNames.add(className);
        return beanNames;
    }

    /**
     * Get the full path of all classes under the package
     * @param file
     */
    private void getAllClassesFullPath(String file) {
        String packageNames = XMLParser.getBasePath(("".equals(file) || null == file ? "springmvc.xml" : file));
        for (String packageName : packageNames.replace(" ", "").split(",")) {
            String packageFullPath = scanPackage(packageName);
            getClassesInPackage(packageName, packageFullPath);
        }
    }

    /**
     * Get the full path of all classes under the package
     * @param packageName
     * @return
     */
    private String scanPackage(String packageName) {
        String packagePath = getPackagePath(packageName);
        return packagePath;
    }

    /**
     * Get the full path of all classes under the package
     * @param packageFullName
     * @return
     */
    private String getPackagePath(String packageFullName) {
        URL url = this.getClass().getClassLoader()
                .getResource(packageFullName.replace(".", "/"));
        return url.getFile();
    }

    /**
     * Get the full path of all classes under the package
     * @param packageName
     * @param packagePath
     */
    private void getClassesInPackage(String packageName, String packagePath) {
        File fileDirectory = new File(packagePath);
        for (File file : fileDirectory.listFiles()) {
            if (file.isDirectory()) {
                getClassesInPackage(packageName + "." + file.getName(), packagePath + "//" + file.getName());
            } else if (file.getName().endsWith(".class")) {
                String fullPath = packageName + "." + file.getName().replace(".class", "");
                classFullPathList.add(fullPath);
            }
        }
    }
}
