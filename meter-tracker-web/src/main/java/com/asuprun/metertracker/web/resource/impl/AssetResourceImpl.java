package com.asuprun.metertracker.web.resource.impl;

import com.asuprun.metertracker.web.filestorage.FileStorage;
import com.asuprun.metertracker.web.repository.ImageInfoRepository;
import com.asuprun.metertracker.web.resource.AssetResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;

@Component
public class AssetResourceImpl implements AssetResource {

    private ImageInfoRepository imageInfoRepository;
    private FileStorage fileStorage;

    @Autowired
    public AssetResourceImpl(ImageInfoRepository imageInfoRepository,
                             FileStorage fileStorage) {
        this.imageInfoRepository = imageInfoRepository;
        this.fileStorage = fileStorage;
    }

    @Override
    public Response getImage(long id) {
        return imageInfoRepository.findById(id)
                .map(i -> {
                    Response.ResponseBuilder response = Response.ok(fileStorage.read(i.toFileMetaData()));
                    response.header("Content-Disposition", "inline; filename=" + i.getFileName());
                    return response.build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}
