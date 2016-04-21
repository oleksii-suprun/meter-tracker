package com.asuprun.metertracker.web.filter;

import com.asuprun.metertracker.web.config.RestConfig;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

@WebFilter("/rs")
public class SwaggerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        URL requestUrl = new URL(request.getRequestURL().toString());
        String swaggerJsonUrl = String.format("%s://%s:%s%s/%s/swagger.json",
                requestUrl.getProtocol(),
                requestUrl.getHost(),
                requestUrl.getPort(),
                request.getContextPath(),
                RestConfig.API_BASE_PATH);
        String url = request.getContextPath() + "/swagger.html?url=" + swaggerJsonUrl;
        response.sendRedirect(url);
    }
}
