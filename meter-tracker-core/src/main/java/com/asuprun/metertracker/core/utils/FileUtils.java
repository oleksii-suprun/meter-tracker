package com.asuprun.metertracker.core.utils;

import java.io.File;

public class FileUtils {

    public static String resolveTilde(String path) {
        if (path.startsWith("~" + File.separator)) {
            return path.replaceFirst("^~", System.getProperty("user.home"));
        }
        return path;
    }
}
