package com.asuprun.metertracker.web.filestorage;

public interface ExceptionTranslator<T> {

    RuntimeException translate(T exception);
}
