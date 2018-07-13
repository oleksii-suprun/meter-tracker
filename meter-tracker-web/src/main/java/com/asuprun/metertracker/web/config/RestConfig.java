package com.asuprun.metertracker.web.config;

import com.asuprun.metertracker.web.config.ApplicationConfig.Profiles;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.models.Info;
import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.spring.JaxRsConfig;
import org.apache.cxf.jaxrs.validation.JAXRSBeanValidationFeature;
import org.apache.cxf.transport.local.LocalConduit;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

import javax.servlet.annotation.WebServlet;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

@Configuration
@ComponentScan(
        basePackages = {
                "com.asuprun.metertracker.web.resource",
                "io.swagger.jaxrs.listing"
        },
        includeFilters = @ComponentScan.Filter({Path.class, Provider.class})
)
public class RestConfig extends JaxRsConfig {

    public static final String API_BASE_PATH = "rs";

    private final ApplicationContext applicationContext;

    @Autowired
    public RestConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public Server jaxRsServer(Bus bus) {
        JAXRSServerFactoryBean factory = new JAXRSServerFactoryBean();
        factory.setAddress(getAddress());
        factory.setBus(bus);
        factory.setServiceBeans(new ArrayList<>(applicationContext.getBeansWithAnnotation(Path.class).values()));
        factory.setProviders(new ArrayList<>(applicationContext.getBeansWithAnnotation(Provider.class).values()));
        factory.setFeatures(Arrays.asList(
                new LoggingFeature(),
                new JAXRSBeanValidationFeature()));
        return factory.create();
    }

    @Bean
    @Profile(Profiles.NOT_TEST)
    public BeanConfig swaggerConfig() {
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setResourcePackage("com.asuprun.metertracker.web.resource");
        beanConfig.setBasePath(API_BASE_PATH);
        beanConfig.setScan(true);
        beanConfig.setInfo(new Info().title("Meter Tracker Application"));
        beanConfig.setSchemes(new String[]{"http"});
        return beanConfig;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Profile(Profiles.TEST)
    public WebClient webClient(JacksonJsonProvider jacksonJsonProvider) {
        WebClient client = WebClient.create(getAddress(), Collections.singletonList(jacksonJsonProvider));
        WebClient.getConfig(client).getRequestContext().put(LocalConduit.DIRECT_DISPATCH, Boolean.TRUE);
        return client;
    }

    @Bean
    public JacksonJsonProvider jacksonJsonProvider(ObjectMapper objectMapper) {
        return new JacksonJsonProvider(objectMapper);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
    }

    private String getAddress() {
        return Stream.of(applicationContext.getEnvironment().getActiveProfiles()).anyMatch(p -> p.equals(Profiles.TEST))
                ? "local://" + API_BASE_PATH
                : "/";
    }

    @WebServlet("/" + API_BASE_PATH + "/*")
    public static class Dispatcher extends CXFServlet {
    }
}
