package com.asuprun.metertracker.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by asuprun on 1/17/15.
 */
public class ImageTracer {

    private static final Logger logger = LoggerFactory.getLogger(ImageTracer.class);
    private static final ImageTracer INSTANCE = new ImageTracer();

    private boolean enabled;
    private String path;

    private ImageTracer() {
        this.enabled = Settings.getInstance().getBoolean("cv.trace.enabled");
        this.path = Settings.getInstance().getString("cv.trace.path");
    }

    public static ImageTracer getInstance() {
        return INSTANCE;
    }

    public void trace(Image image, String suffix) {
        if (!enabled) {
            return;
        }

        File file = new File(buildFilename(suffix));
        try {
            file.getParentFile().mkdirs();
            ImageIO.write((RenderedImage) image, "jpeg", file);
        } catch (IOException e) {
            logger.error("Unable to trace image '{}'", file);
        }
    }

    private String buildFilename(String filename) {
        StringBuilder builder = new StringBuilder(path);
        builder.append("/");
        builder.append(System.currentTimeMillis()).append("-");
        if (StringUtils.isNotBlank(filename)) {
            builder.append(filename).append("-");
        }
        builder.append("log");
        builder.append(".jpg");
        return builder.toString();
    }
}
