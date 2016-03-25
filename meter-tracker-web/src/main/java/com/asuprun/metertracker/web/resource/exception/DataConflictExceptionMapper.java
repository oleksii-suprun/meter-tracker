package com.asuprun.metertracker.web.resource.exception;

import com.asuprun.metertracker.web.exception.DataConflictException;
import com.asuprun.metertracker.web.resource.response.ErrorResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class DataConflictExceptionMapper implements ExceptionMapper<DataConflictException> {

    @Override
    public Response toResponse(DataConflictException exception) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorResponse(exception.getMessage()))
                .build();
    }
}
