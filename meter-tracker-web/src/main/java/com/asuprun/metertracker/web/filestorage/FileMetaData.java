package com.asuprun.metertracker.web.filestorage;

import java.util.Date;
import java.util.Objects;

public class FileMetaData {

    private String fileId;
    private String hash;
    private String fileName;
    private Date createdAt;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileMetaData that = (FileMetaData) o;
        return Objects.equals(fileId, that.fileId) &&
                Objects.equals(hash, that.hash) &&
                Objects.equals(fileName, that.fileName) &&
                Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {

        return Objects.hash(fileId, hash, fileName, createdAt);
    }
}
