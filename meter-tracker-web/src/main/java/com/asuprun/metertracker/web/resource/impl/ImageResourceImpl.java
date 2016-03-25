package com.asuprun.metertracker.web.resource.impl;

import com.asuprun.metertracker.web.domain.Resource;
import com.asuprun.metertracker.web.repository.ResourceRepository;
import com.asuprun.metertracker.web.resource.ImageResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;

@Component
public class ImageResourceImpl implements ImageResource {

    @Autowired
    private ResourceRepository resourceRepository;

    @Override
    public Response getImage(String id) {
        Resource image = resourceRepository.findOne(Long.valueOf(id));
        if (image == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Response.ResponseBuilder response = Response.ok(image.getData());
        response.header("Content-Disposition", "inline; filename=" + id + ".jpeg");
        return response.build();
    }
}
