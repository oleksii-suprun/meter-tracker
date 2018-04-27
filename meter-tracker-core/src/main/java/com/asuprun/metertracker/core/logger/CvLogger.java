package com.asuprun.metertracker.core.logger;

import org.opencv.core.Mat;

import java.awt.*;

public interface CvLogger {

    Level level();

    void trace(Image image, String tag);

    void debug(Image image, String tag);

    void info(Image image, String tag);

    void warn(Image image, String tag);

    void error(Image image, String tag);

    void trace(Mat mat, String tag);

    void debug(Mat mat, String tag);

    void info(Mat mat, String tag);

    void warn(Mat mat, String tag);

    void error(Mat mat, String tag);

    boolean isTraceEnabled();

    boolean isDebugEnabled();

    boolean isInfoEnabled();

    boolean isWarnEnabled();

    boolean isErrorEnabled();

    boolean isEnabled();

    enum Level {
        ALL,
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        OFF
    }
}
