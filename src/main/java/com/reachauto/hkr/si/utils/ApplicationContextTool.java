package com.reachauto.hkr.si.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextTool implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static Object get(Class clazz) {
        return applicationContext.getBean(clazz);
    }

    public static Object get(String name) {
        return applicationContext.getBean(name);
    }

    @Override
    public synchronized void setApplicationContext(ApplicationContext applicationContext) {
        ApplicationContextTool.applicationContext = applicationContext;
    }
}