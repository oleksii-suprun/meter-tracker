package com.asuprun.metertracker.web.utils;

import java.util.function.Function;

public class Exceptions {

    public static <T, R> Function<T, R> unchecked(ThrowingFunction<T, R> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Exception ex) {
                return rethrow(ex);
            }
        };
    }

    @SuppressWarnings("unchecked")
    private static <E extends Exception, R> R rethrow(Exception exception) throws E {
        throw (E) exception;
    }

    public interface ThrowingFunction<T, R> {

        R apply(T t) throws Exception;
    }
}
