package com.asuprun.metertracker.web.filestorage;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;

import java.util.NoSuchElementException;

public class GoogleJsonResponseExceptionTranslator
        implements ExceptionTranslator<GoogleJsonResponseException> {

    @Override
    public RuntimeException translate(GoogleJsonResponseException exception) {
        GoogleJsonError error = exception.getDetails();
        switch (error.getCode()) {
            case 404:
                return new NoSuchElementException(error.getMessage());
            default:
                return new RuntimeException("Unexpected Google Drive exception.", exception);
        }
    }
}
