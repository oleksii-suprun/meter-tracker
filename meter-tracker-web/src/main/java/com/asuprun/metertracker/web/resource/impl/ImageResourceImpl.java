package com.asuprun.metertracker.web.resource.impl;

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
        return resourceRepository.findById(Long.valueOf(id))
                .map(i -> {
                    Response.ResponseBuilder response = Response.ok(i.getData());
                    response.header("Content-Disposition", "inline; filename=" + id + ".jpeg");
                    return response.build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }
}
