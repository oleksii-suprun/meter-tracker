package com.asuprun.metertracker.web.config;

import com.asuprun.metertracker.web.filestorage.FileStorage;
import com.asuprun.metertracker.web.filestorage.LocalFileStorage;
import org.opencv.core.Core;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.nio.file.Paths;
import java.util.Properties;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan("com.asuprun.metertracker.web.service")
public class ApplicationConfig {

    static {
        System.setProperty("java.awt.headless", "true");
        System.load(Paths.get(System.getenv("OPENCV_JAVA_PATH"))
                .resolve(System.mapLibraryName(Core.NATIVE_LIBRARY_NAME)).toString());
    }

    @Value("${application.fs.local.path}")
    private String fileStoragePath;

    @Value("${application.fs.local.bucket.name_length}")
    private int bucketPathLength;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new CommonsMultipartResolver();
    }

    @Bean
    public FileStorage fileStorage() {
        return new LocalFileStorage(new Properties() {{
            setProperty(LocalFileStorage.FS_PATH, fileStoragePath);
            setProperty(LocalFileStorage.FS_BUCKET_NAME_LENGTH, String.valueOf(bucketPathLength));
        }});
    }

    public interface Profiles {
        String TEST = "test";
        String NOT_TEST = "!test";
    }
}
