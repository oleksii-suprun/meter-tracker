package com.asuprun.metertracker.web.filestorage;

import java.util.Objects;
import java.util.Properties;

public abstract class FileStorage {

    private Properties properties;

    public FileStorage(Properties properties) {
        this.properties = Objects.requireNonNull(properties);
    }

    public Properties getProperties() {
        return properties;
    }

    public abstract byte[] read(FileMetaData metaData);

    public abstract void delete(FileMetaData metaData);

    public abstract FileMetaData save(byte[] data, String fileName);
}
