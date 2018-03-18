package com.asuprun.metertracker.web.servlet;

import com.asuprun.metertracker.web.filestorage.FileStorage;
import com.asuprun.metertracker.web.filestorage.LocalFileStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.asuprun.metertracker.web.servlet.FileStorageServlet.PATH;

@WebServlet(PATH)
public class FileStorageServlet extends HttpServlet {

    public static final String PATH = "/" + LocalFileStorage.ACCESS_URL;

    @Autowired
    private FileStorage fileStorage;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, String> params = Arrays.stream(req.getQueryString().split("&"))
                .map(s -> s.split("="))
                .collect(Collectors.toMap(a -> a[0], a -> a[1]));

        resp.getOutputStream().write(Optional.of(params.get("id"))
                .map(id -> fileStorage.read(id))
                .orElseThrow(() -> new NoSuchElementException("File not found")));
    }
}
