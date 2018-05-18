package com.asuprun.metertracker.web.filestorage;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.asuprun.metertracker.core.utils.FileUtils.resolveTilde;
import static java.lang.String.format;

public class GDriveFileStorage implements FileStorage {
    private static final Logger logger = LoggerFactory.getLogger(GDriveFileStorage.class);

    private static final SimpleDateFormat CREATED_DATE_FORMAT = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    private static final String[] GOOGLE_DRIVE_SCOPES = new String[]{DriveScopes.DRIVE};

    private final GoogleJsonResponseExceptionTranslator translator = new GoogleJsonResponseExceptionTranslator();

    private final HttpTransport httpTransport;
    private final JsonFactory jsonFactory;
    private final String homeDirectoryId;
    private final java.io.File serviceAccountKeyJson;

    public GDriveFileStorage(String googleDriveDirectory,
                             String serviceAccountKeyJson) {
        try {
            this.jsonFactory = JacksonFactory.getDefaultInstance();
            this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            this.serviceAccountKeyJson = new java.io.File(resolveTilde(serviceAccountKeyJson));
            this.homeDirectoryId = findHomeDirectory(googleDriveDirectory).getId();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File findHomeDirectory(String homeDirectory) throws IOException {
        List<File> gfiles = driveService()
                .files()
                .list()
                .setQ(format("name = '%s' AND mimeType = 'application/vnd.google-apps.folder'", homeDirectory))
                .execute()
                .getFiles();

        if (gfiles.isEmpty()) {
            throw new IllegalArgumentException(format("No drive directory with name '%s' found", homeDirectory));
        }
        if (gfiles.size() > 1) {
            throw new IllegalArgumentException(format("Multiple drive directories named '%s' found", homeDirectory));
        }

        Optional<Permission> permission = driveService().permissions()
                .list(gfiles.get(0).getId())
                .execute()
                .getPermissions()
                .stream()
                .filter(p -> "reader".equalsIgnoreCase(p.getRole()) && p.getId().toLowerCase().contains("anyone"))
                .findFirst();
        if (!permission.isPresent()) {
            throw new IllegalStateException(format("Drive directory '%s' must be shared", homeDirectory));
        }

        return gfiles.get(0);
    }

    @Override
    public byte[] read(String fileId) {
        try {
            InputStream inputStream = driveService()
                    .files()
                    .get(fileId)
                    .executeMediaAsInputStream();
            return IOUtils.toByteArray(inputStream);
        } catch (GoogleJsonResponseException e) {
            logger.error(String.format("Unable to read file from Google Drive (id=%s)", fileId), e);
            throw translator.translate(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String fileId) {
        try {
            driveService().files().delete(fileId).execute();
        } catch (GoogleJsonResponseException e) {
            logger.error(String.format("Unable to delete file from Google Drive (id=%s)", fileId), e);
            throw translator.translate(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileMetaData save(byte[] data, String fileName) {
        File fileMetadata = new File();
        fileMetadata.setName(fileName);
        fileMetadata.setParents(Collections.singletonList(homeDirectoryId));

        try {
            ByteArrayContent mediaContent = new ByteArrayContent(null, data);
            File file = driveService().files()
                    .create(fileMetadata, mediaContent)
                    .setFields("id,md5Checksum,originalFilename,imageMediaMetadata,webContentLink,createdTime")
                    .execute();

            Date createdAt = file.getImageMediaMetadata().getTime() != null
                    ? CREATED_DATE_FORMAT.parse(file.getImageMediaMetadata().getTime())
                    : null;

            FileMetaData fileMetaData = new FileMetaData();
            fileMetaData.setId(file.getId());
            fileMetaData.setCreatedAt(createdAt);
            fileMetaData.setUploadedAt(new Date(file.getCreatedTime().getValue()));
            fileMetaData.setHash(file.getMd5Checksum());
            fileMetaData.setName(file.getOriginalFilename());
            fileMetaData.setUrl(file.getWebContentLink());
            return fileMetaData;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Drive driveService() throws IOException {
        Credential credential = GoogleCredential
                .fromStream(new FileInputStream(serviceAccountKeyJson))
                .createScoped(Arrays.asList(GOOGLE_DRIVE_SCOPES));

        return new Drive.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("Meter Tracker")
                .build();
    }
}
