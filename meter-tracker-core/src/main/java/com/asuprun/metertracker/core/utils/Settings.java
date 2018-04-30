package com.asuprun.metertracker.core.utils;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

/**
 * Singleton for application settings access. This class represents common interface for retrieve properties
 * defined in 'cv.properties' file and provides typed methods for avoid class casting in main code. Described file has
 * to be placed in classpath.
 *
 * @author asuprun
 * @since 1.0
 */
public final class Settings {

    private static final String PROPERTIES_FILE = "cv.properties";

    private static volatile Settings instance;
    private Properties properties;

    /**
     * Private constructor
     */
    private Settings() {
        properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Object accessor. Returns only one object per JVM.
     *
     * @return instance of {@code Settings} class
     */
    public static Settings getInstance() {
        if (instance == null) {
            synchronized (Settings.class) {
                if (instance == null) {
                    instance = new Settings();
                }
            }
        }
        return instance;
    }

    /**
     * Returns int value by provided key
     *
     * @param key string key for defined property
     * @return integer value defined with provided key
     */
    public int getInt(String key) {
        return Integer.valueOf(properties.getProperty(key));
    }

    /**
     * Returns boolean value by provided key
     *
     * @param key string key for defined property
     * @return boolean value defined with provided key
     */
    public boolean getBoolean(String key) {
        return Boolean.valueOf(properties.getProperty(key));
    }

    /**
     * Returns string value by provided key
     *
     * @param key string key for defined property
     * @return string value or null if there is no property with provided key
     */
    public Optional<String> getString(String key) {
        return Optional.ofNullable(properties.getProperty(key));
    }
}
