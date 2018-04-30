package com.asuprun.metertracker.core.logger;

import org.opencv.core.Mat;

import java.awt.*;

public final class NoOpCvLogger extends AbstractCvLogger {

    public NoOpCvLogger() {
        super(Level.OFF);
    }

    @Override
    public void trace(Image image, String tag) {
    }

    @Override
    public void debug(Image image, String tag) {
    }

    @Override
    public void info(Image image, String tag) {
    }

    @Override
    public void warn(Image image, String tag) {
    }

    @Override
    public void error(Image image, String tag) {
    }

    @Override
    public void trace(Mat mat, String tag) {
    }

    @Override
    public void debug(Mat mat, String tag) {
    }

    @Override
    public void info(Mat mat, String tag) {
    }

    @Override
    public void warn(Mat mat, String tag) {
    }

    @Override
    public void error(Mat mat, String tag) {
    }

    @Override
    protected void writeLog(Image image, Level level, String tag) {
    }
}
