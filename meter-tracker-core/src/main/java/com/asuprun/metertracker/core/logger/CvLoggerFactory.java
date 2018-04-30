package com.asuprun.metertracker.core.logger;

import com.asuprun.metertracker.core.logger.CvLogger.Level;
import com.asuprun.metertracker.core.utils.FileStorageUtils;
import com.asuprun.metertracker.core.utils.Settings;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class CvLoggerFactory {

    private static final String DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATETIME_PATTERN);


    public static CvLogger getLogger(String bucket) {
        Level level = Level.valueOf(Settings.getInstance().getString("cv.log.level")
                .orElse(Level.OFF.name()));

        Path rootDirectory = Settings.getInstance().getString("cv.log.directory")
                .map(FileStorageUtils::resolveTilde)
                .map(Paths::get)
                .orElse(null);

        return new DefaultCvLogger(level, rootDirectory, DATE_FORMAT.format(new Date()) + "_" + bucket);
    }
}
