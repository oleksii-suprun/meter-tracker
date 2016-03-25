package com.asuprun.metertracker.web.config;

import org.springframework.web.context.AbstractContextLoaderInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class WebInitializer extends AbstractContextLoaderInitializer {

    private static final Class<?>[] configs = {
            ApplicationConfig.class,
            RepositoryConfig.class,
            RestConfig.class
    };

    @Override
    protected WebApplicationContext createRootApplicationContext() {
        AnnotationConfigWebApplicationContext rootAppContext = new AnnotationConfigWebApplicationContext();
        rootAppContext.register(configs);
        return rootAppContext;
    }
}