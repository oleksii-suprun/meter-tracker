package com.asuprun.metertracker.web.resource.response;

public class ErrorResponse {

    private String message;

    @SuppressWarnings("unused")
    public ErrorResponse() {
    }

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
