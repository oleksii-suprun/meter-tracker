package com.asuprun.metertracker.web.filestorage;

import com.asuprun.metertracker.web.exception.DataConflictException;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class LocalFileStorage extends FileStorage {
    private static final Logger logger = LoggerFactory.getLogger(FileStorage.class);

    public static final String FS_PATH = "fs.path";
    public static final String FS_BUCKET_NAME_LENGTH = "fs.bucketname-length";

    public LocalFileStorage(Properties properties) {
        super(properties);

        if (properties.getProperty(FS_PATH) == null) {
            throw new RuntimeException("'fs.path' not configured");
        }
    }

    private String getRoot() {
        return getProperties().getProperty(FS_PATH).replaceFirst("^~", System.getProperty("user.home"));
    }

    private int getBucketNameLength() {
        return Integer.parseInt(getProperties().getProperty(FS_BUCKET_NAME_LENGTH));
    }

    @Override
    public synchronized byte[] read(FileMetaData metaData) {
        String id = metaData.getFileId();
        Path bucketPath = Paths.get(getRoot(), id.substring(0, getBucketNameLength()));
        if (!Files.exists(bucketPath)) {
            throw new IllegalStateException(new NoSuchFileException("No file found by id " + id));
        }

        try {
            Path filePath = Files.list(bucketPath).filter(p -> p.getFileName().toString().startsWith(id))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(new NoSuchFileException("No file found by id " + id)));
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            String message = String.format("Unable to read file %s", bucketPath.toString());
            logger.error(message, e);
            throw new RuntimeException(message);
        }
    }

    @Override
    public synchronized void delete(FileMetaData metaData) {
        String id = metaData.getFileId();
        Path bucketPath = Paths.get(getRoot(), metaData.getFileId().substring(0, getBucketNameLength()));
        if (!Files.exists(bucketPath)) {
            throw new IllegalStateException(new NoSuchFileException("No file found by id " + id));
        }

        try {
            Files.deleteIfExists(Files.list(bucketPath).filter(p -> p.getFileName().toString().startsWith(id))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No file found by id " + id)));
            if (!Files.list(bucketPath).findFirst().isPresent()) {
                Files.delete(bucketPath);
            }
        } catch (IOException e) {
            String message = String.format("Unable to delete file %s", bucketPath.toString());
            logger.error(message, e);
            throw new RuntimeException(message);
        }
    }

    @Override
    public synchronized FileMetaData save(byte[] data, String fileName) {
        String hash = DigestUtils.md5DigestAsHex(data);
        Path bucket = Paths.get(getRoot(), hash.substring(0, getBucketNameLength()));
        Path filePath = Paths.get(bucket.toString(), hash + fileName.substring(fileName.indexOf('.')));

        try {
            Files.createDirectories(bucket);
            Files.write(filePath, data, StandardOpenOption.CREATE_NEW);
        } catch (FileAlreadyExistsException e) {
            throw new DataConflictException(String.format("File '%s' already exists in the system", fileName));
        } catch (IOException e) {
            String errorMessage = String.format("Unable to save file '%s'", fileName);
            logger.error(errorMessage, e);
            throw new RuntimeException(errorMessage);
        }

        Date createdAt = readCreatedFromFileMetadata(data).orElse(null);

        return new FileMetaData() {{
            setHash(hash);
            setFileName(fileName);
            setFileId(hash);
            setCreatedAt(createdAt);
        }};
    }

    private Optional<Metadata> readMetadata(byte[] bytes) {
        try {
            return Optional.of(ImageMetadataReader.readMetadata(new ByteArrayInputStream(bytes)));
        } catch (ImageProcessingException | IOException e) {
            logger.warn("Cannot read image metadata");
            logger.debug("Cannot read image metadata", e);
        }
        return Optional.empty();
    }

    private Optional<Date> readCreatedFromFileMetadata(byte[] bytes) {
        return readMetadata(bytes)
                .map(m -> m.getFirstDirectoryOfType(ExifSubIFDDirectory.class))
                .map(d -> d.getDateOriginal(TimeZone.getDefault()));
    }
}
