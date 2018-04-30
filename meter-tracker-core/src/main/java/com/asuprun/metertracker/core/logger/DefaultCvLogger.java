package com.asuprun.metertracker.core.logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DefaultCvLogger extends AbstractCvLogger {

    private Path rootDirectory;
    private String bucket;

    DefaultCvLogger(Level level, Path rootDirectory, String bucket) {
        super(level);
        this.rootDirectory = rootDirectory;
        this.bucket = bucket;
    }

    @Override
    protected void writeLog(Image image, Level level, String tag) {
        try {
            String fileName = String.format("%s-%s-%s.jpg", System.nanoTime(), level.name(), tag);
            Path bucketDirectory = Files.createDirectories(Paths.get(rootDirectory.toString(), bucket));
            Path filePath = Paths.get(bucketDirectory.toString(), fileName);
            ImageIO.write((RenderedImage) image, "jpeg", new File(filePath.toUri()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
