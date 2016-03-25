package com.asuprun.metertracker.core.exception;

/**
 * Created by asuprun on 2/21/15.
 */
public class RecognitionException extends Exception {

    public RecognitionException(String message, Exception exc) {
        super(message, exc);
    }
}
