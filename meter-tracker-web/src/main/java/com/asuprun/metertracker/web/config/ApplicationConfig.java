package com.asuprun.metertracker.web.config;

import com.asuprun.metertracker.web.filestorage.FileStorage;
import com.asuprun.metertracker.web.filestorage.GDriveFileStorage;
import com.asuprun.metertracker.web.filestorage.LocalFileStorage;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import static com.asuprun.metertracker.web.config.ApplicationConfig.Profiles.NOT_TEST;
import static com.asuprun.metertracker.web.config.ApplicationConfig.Profiles.TEST;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan("com.asuprun.metertracker.web.service")
public class ApplicationConfig {

    static {
        System.setProperty("java.awt.headless", "true");
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new CommonsMultipartResolver();
    }

    @Bean
    @Profile(TEST)
    public FileStorage localFileStorage(Environment environment) {
        return new LocalFileStorage(environment.getProperty("application.fs.local.path"));
    }

    @Bean
    @Profile(NOT_TEST)
    public FileStorage fileStorage(Environment environment) {
        return new GDriveFileStorage(
                environment.getProperty("application.fs.gdrive.directory"),
                environment.getProperty("application.fs.gdrive.serviceAccountKey")
        );
    }

    public interface Profiles {
        String TEST = "test";
        String NOT_TEST = "!test";
    }
}
