package com.asuprun.metertracker.web.filestorage;

import java.io.File;

public class FileStorageUtils {

    public static String resolveTilde(String path) {
        if (path.startsWith("~" + File.separator)) {
            return path.replaceFirst("^~", System.getProperty("user.home"));
        }
        return path;
    }
}
