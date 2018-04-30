package com.asuprun.metertracker.web.filestorage;

import com.asuprun.metertracker.core.utils.FileStorageUtils;
import com.asuprun.metertracker.web.exception.DataConflictException;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TimeZone;

import static com.asuprun.metertracker.web.utils.Exceptions.unchecked;

public class LocalFileStorage implements FileStorage {
    private static final Logger logger = LoggerFactory.getLogger(FileStorage.class);

    public static final int BUCKET_NAME_LENGTH = 3;
    public static final String ACCESS_URL = "files";

    private final String homeDirectory;

    public LocalFileStorage(String homeDirectory) {
        this.homeDirectory = Optional.ofNullable(homeDirectory)
                .filter(StringUtils::isNotEmpty)
                .map(FileStorageUtils::resolveTilde)
                .orElseThrow(() -> new IllegalStateException("Home directory for file storage is not configured"));
    }

    @Override
    public synchronized byte[] read(String fileId) {
        Path bucketPath = Paths.get(homeDirectory, fileId.substring(0, BUCKET_NAME_LENGTH));
        if (!Files.exists(bucketPath)) {
            throw new NoSuchElementException("No file found by id " + fileId);
        }

        try {
            return Files.list(bucketPath)
                    .filter(p -> p.getFileName().toString().startsWith(fileId))
                    .findFirst()
                    .map(unchecked(Files::readAllBytes))
                    .orElseThrow(() -> new NoSuchElementException("No file found by id " + fileId));
        } catch (IOException e) {
            String message = String.format("Unable to read file %s", bucketPath.toString());
            logger.error(message, e);
            throw new RuntimeException(message);
        }
    }

    @Override
    public synchronized void delete(String fileId) {
        Path bucketPath = Paths.get(homeDirectory, fileId.substring(0, BUCKET_NAME_LENGTH));
        if (!Files.exists(bucketPath)) {
            throw new NoSuchElementException("No file found by id " + fileId);
        }

        try {
            Files.deleteIfExists(Files.list(bucketPath)
                    .filter(p -> p.getFileName().toString().startsWith(fileId))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("No file found by id " + fileId)));
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
        Path bucket = Paths.get(homeDirectory, hash.substring(0, BUCKET_NAME_LENGTH));
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

        FileMetaData fileMetaData = new FileMetaData();
        fileMetaData.setHash(hash);
        fileMetaData.setName(fileName);
        fileMetaData.setId(hash);
        fileMetaData.setCreatedAt(createdAt);
        fileMetaData.setUploadedAt(new Date());
        fileMetaData.setUrl(ACCESS_URL + "?id=" + fileMetaData.getId()); // remove leading slash from url
        return fileMetaData;
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
