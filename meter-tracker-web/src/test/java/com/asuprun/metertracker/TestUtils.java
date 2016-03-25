package com.asuprun.metertracker;

import com.asuprun.metertracker.web.resource.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.fail;

public abstract class TestUtils {

    public static ErrorResponse readErrorResponseEntity(Response response) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(((InputStream) response.getEntity()), ErrorResponse.class);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        return null;
    }
}
