package com.asuprun.metertracker.web.config;

import org.opencv.core.Core;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.nio.file.Paths;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan("com.asuprun.metertracker.web.service")
public class ApplicationConfig {

    static {
        System.setProperty("java.awt.headless", "true");
        System.load(Paths.get(System.getenv("OPENCV_JAVA_PATH"))
                .resolve(System.mapLibraryName(Core.NATIVE_LIBRARY_NAME)).toString());
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new CommonsMultipartResolver();
    }

    public interface Profiles {
        String TEST = "test";
        String NOT_TEST = "!test";
    }
}
