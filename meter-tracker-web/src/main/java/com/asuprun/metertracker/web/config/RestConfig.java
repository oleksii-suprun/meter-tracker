package com.asuprun.metertracker.web.config;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.models.Info;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.spring.SpringComponentScanServer;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Configuration
@ComponentScan(
        basePackages = {
                "com.asuprun.metertracker.web.resource",
                "io.swagger.jaxrs.listing"
        },
        includeFilters = @ComponentScan.Filter({Path.class, Provider.class})
)
public class RestConfig extends SpringComponentScanServer {

    public static final String API_BASE_PATH = "rs";
    public static final String LOCAL_TRANSPORT_PATH = "local://" + API_BASE_PATH;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    @Profile(ApplicationConfig.Profiles.NOT_TEST)
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
    @Profile(ApplicationConfig.Profiles.TEST)
    public WebClient webClient() {
        WebClient client = WebClient.create(getAddress(), getJaxrsProviders());
        WebClient.getConfig(client).getRequestContext().put(LocalConduit.DIRECT_DISPATCH, Boolean.TRUE);
        return client;
    }

    @Override
    public List<Feature> getFeatures() {
        return Arrays.asList(
                new LoggingFeature(),
                new JAXRSBeanValidationFeature());
    }

    @Override
    protected List<Object> getJaxrsProviders() {
        List<Object> providers = super.getJaxrsProviders();
        providers.add(new JacksonJsonProvider());
        return providers;
    }

    @Override
    protected String getAddress() {
        return Stream.of(applicationContext.getEnvironment().getActiveProfiles()).anyMatch(p -> p.equals("test"))
                ? LOCAL_TRANSPORT_PATH
                : super.getAddress();
    }

    @WebServlet("/" + API_BASE_PATH + "/*")
    public static class Dispatcher extends CXFServlet {
    }
}
