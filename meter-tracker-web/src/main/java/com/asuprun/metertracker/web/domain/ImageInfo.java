package com.asuprun.metertracker.web.domain;

import com.asuprun.metertracker.web.filestorage.FileMetaData;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@SuppressWarnings("unused")
@Entity(name = "image_info")
public class ImageInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_info_id_seq")
    @SequenceGenerator(name = "image_info_id_seq", sequenceName = "image_info_id_seq", allocationSize = 1)
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

    @Column
    private String url;

    public ImageInfo() {
    }

    public ImageInfo(FileMetaData fileMetaData) {
        this.storageId = fileMetaData.getId();
        this.hash = fileMetaData.getHash();
        this.url = fileMetaData.getUrl();
        this.fileName = fileMetaData.getName();
        this.createdAt = fileMetaData.getCreatedAt();
        this.uploadedAt = fileMetaData.getUploadedAt();
    }

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
                Objects.equals(createdAt, imageInfo.createdAt) &&
                Objects.equals(url, imageInfo.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, storageId, hash, fileName, uploadedAt, createdAt, url);
    }

    public final FileMetaData toFileMetaData() {
        FileMetaData fileMetaData = new FileMetaData();
        fileMetaData.setId(storageId);
        fileMetaData.setName(fileName);
        fileMetaData.setHash(hash);
        fileMetaData.setUrl(url);
        fileMetaData.setCreatedAt(createdAt);
        fileMetaData.setUploadedAt(uploadedAt);
        return fileMetaData;
    }
}
