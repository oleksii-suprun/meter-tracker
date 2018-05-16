package com.asuprun.metertracker.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_java;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public final class OpenCvLoader {

    private static final AtomicBoolean loaded = new AtomicBoolean(false);

    public static synchronized void load() {
        loaded.compareAndSet(false, Optional.ofNullable(Loader.load(opencv_java.class))
                .map(StringUtils::isNotBlank)
                .orElseThrow(() -> new IllegalStateException("Unable to load native library")));
    }
}
