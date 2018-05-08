package com.asuprun.metertracker.web.config;

import com.asuprun.metertracker.web.filestorage.FileStorage;
import com.asuprun.metertracker.web.filestorage.GDriveFileStorage;
import com.asuprun.metertracker.web.filestorage.LocalFileStorage;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import org.opencv.core.Core;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;

import static com.asuprun.metertracker.core.utils.FileUtils.resolveTilde;
import static com.asuprun.metertracker.web.config.ApplicationConfig.Profiles.NOT_TEST;
import static com.asuprun.metertracker.web.config.ApplicationConfig.Profiles.TEST;

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

    @Bean
    @Profile(TEST)
    public FileStorage localFileStorage(Environment environment) {
        return new LocalFileStorage(environment.getProperty("application.fs.local.path"));
    }

    @Bean
    @Profile(NOT_TEST)
    public DataStoreFactory dataStoreFactory(Environment environment) throws IOException {
        String credentialsStorageDirectory = Objects.requireNonNull(
                environment.getProperty("application.fs.gdrive.credentials.directory"));

        return new FileDataStoreFactory(new File(resolveTilde(credentialsStorageDirectory)));
    }

    @Bean
    @Profile(NOT_TEST)
    public FileStorage fileStorage(Environment environment,
                                   DataStoreFactory dataStoreFactory) {
        return new GDriveFileStorage(
                environment.getProperty("application.fs.gdrive.directory"),
                environment.getProperty("application.fs.gdrive.secrets"),
                dataStoreFactory);
    }

    public interface Profiles {
        String TEST = "test";
        String NOT_TEST = "!test";
    }
}
