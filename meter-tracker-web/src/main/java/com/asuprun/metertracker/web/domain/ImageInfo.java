package com.asuprun.metertracker.web.domain;

import com.asuprun.metertracker.web.filestorage.FileMetaData;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@SuppressWarnings("unused")
@Entity(name = "image_info")
public class ImageInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "storage_id")
    private String storageId;

    @Column
    private String hash;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "uploaded_at")
    private Date uploadedAt;

    @Column(name = "created_at")
    private Date createdAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStorageId() {
        return storageId;
    }

    public void setStorageId(String storageId) {
        this.storageId = storageId;
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

    public Date getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Date uploadedAt) {
        this.uploadedAt = uploadedAt;
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
        ImageInfo imageInfo = (ImageInfo) o;
        return id == imageInfo.id &&
                Objects.equals(storageId, imageInfo.storageId) &&
                Objects.equals(hash, imageInfo.hash) &&
                Objects.equals(fileName, imageInfo.fileName) &&
                Objects.equals(uploadedAt, imageInfo.uploadedAt) &&
                Objects.equals(createdAt, imageInfo.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, storageId, hash, fileName, uploadedAt, createdAt);
    }

    public final FileMetaData toFileMetaData() {
        FileMetaData fileMetaData = new FileMetaData();
        fileMetaData.setFileId(storageId);
        fileMetaData.setFileName(fileName);
        fileMetaData.setHash(hash);
        return fileMetaData;
    }
}
