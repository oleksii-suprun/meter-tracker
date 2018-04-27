package com.asuprun.metertracker.core.logger;

import org.opencv.core.Mat;

import java.awt.*;

import static com.asuprun.metertracker.core.utils.ImageUtils.matToImage;

public abstract class AbstractCvLogger implements CvLogger {

    private Level level;

    public AbstractCvLogger(Level level) {
        this.level = level;
    }

    @Override
    public Level level() {
        return level;
    }

    @Override
    public void trace(Image image, String tag) {
        log(image, Level.TRACE, tag);
    }

    @Override
    public void debug(Image image, String tag) {
        log(image, Level.DEBUG, tag);
    }

    @Override
    public void info(Image image, String tag) {
        log(image, Level.INFO, tag);
    }

    @Override
    public void warn(Image image, String tag) {
        log(image, Level.WARN, tag);
    }

    @Override
    public void error(Image image, String tag) {
        log(image, Level.ERROR, tag);
    }

    @Override
    public void trace(Mat mat, String tag) {
        log(mat, Level.TRACE, tag);
    }

    @Override
    public void debug(Mat mat, String tag) {
        log(mat, Level.DEBUG, tag);
    }

    @Override
    public void info(Mat mat, String tag) {
        log(mat, Level.INFO, tag);
    }

    @Override
    public void warn(Mat mat, String tag) {
        log(mat, Level.WARN, tag);
    }

    @Override
    public void error(Mat mat, String tag) {
        log(mat, Level.ERROR, tag);
    }

    @Override
    public boolean isTraceEnabled() {
        return level.ordinal() <= Level.TRACE.ordinal();
    }

    @Override
    public boolean isDebugEnabled() {
        return level.ordinal() <= Level.DEBUG.ordinal();
    }

    @Override
    public boolean isInfoEnabled() {
        return level.ordinal() <= Level.INFO.ordinal();
    }

    @Override
    public boolean isWarnEnabled() {
        return level.ordinal() <= Level.WARN.ordinal();
    }

    @Override
    public boolean isErrorEnabled() {
        return level.ordinal() <= Level.ERROR.ordinal();
    }

    @Override
    public boolean isEnabled() {
        return level != Level.OFF;
    }

    protected void log(Mat mat, Level level, String tag) {
        if (this.level.ordinal() <= level.ordinal()) {
            log(matToImage(mat), level, tag);
        }
    }

    protected void log(Image image, Level level, String tag) {
        if (this.level.ordinal() <= level.ordinal()) {
            writeLog(image, level, tag);
        }
    }

    protected abstract void writeLog(Image image, Level level, String tag);
}
