package com.asuprun.metertracker.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import org.webjars.WebJarAssetLocator;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@WebFilter("/webjars/*")
public class WebJarsFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(WebJarsFilter.class);

    private WebJarAssetLocator locator = new WebJarAssetLocator();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestUrl = request.getRequestURL().toString();
        String path = request.getRequestURL().toString().substring(requestUrl.indexOf("/webjars"));
        Optional<String> webjar = Arrays.stream(path.split("/"))
                .filter(x -> locator.getWebJars().containsKey(x))
                .findFirst();

        if (webjar.isPresent()) {
            String webjarString = webjar.get();
            String nameWithVersion = webjarString + "/" + locator.getWebJars().get(webjarString);
            if (!path.contains(nameWithVersion)) {
                path = path.replaceFirst(webjarString, nameWithVersion);
            }
        } else {
            logger.warn("Provided url is not webjar url: {}", path);
            return;
        }
        request.getRequestDispatcher(path).forward(request, response);
    }
}
