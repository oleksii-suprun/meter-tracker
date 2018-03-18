package com.asuprun.metertracker.web.filestorage;

public interface FileStorage {

    byte[] read(String fileId);

    void delete(String fileId);

    FileMetaData save(byte[] data, String fileName);
}
