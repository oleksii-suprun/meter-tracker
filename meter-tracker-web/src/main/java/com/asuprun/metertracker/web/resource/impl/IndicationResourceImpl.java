package com.asuprun.metertracker.web.resource.impl;

import com.asuprun.metertracker.web.domain.Indication;
import com.asuprun.metertracker.web.dto.DigitDto;
import com.asuprun.metertracker.web.dto.IndicationDto;
import com.asuprun.metertracker.web.resource.IndicationResource;
import com.asuprun.metertracker.web.service.IndicationService;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class IndicationResourceImpl implements IndicationResource {

    private static Logger logger = LoggerFactory.getLogger(IndicationResource.class);

    private IndicationService indicationService;

    @Value("${application.allowed.extensions}")
    private String[] allowedExtensions;

    @Autowired
    public IndicationResourceImpl(IndicationService indicationService) {
        this.indicationService = indicationService;
    }

    @Override
    public List<IndicationDto> get(Long meterId, Boolean unrecognized) {
        return IndicationDto.toDtos(indicationService.findByTypeAndState(meterId, unrecognized));
    }

    @Override
    public Response get(long id) {
        return indicationService.findById(id)
                .map(i -> Response.ok(IndicationDto.toDto(i)).build())
                .orElseThrow(() -> {
                    logger.debug("No indication found for id: {}", id);
                    return new NoSuchElementException("No such indication");
                });
    }

    @Override
    public Response delete(long id) {
        indicationService.delete(id);
        return Response.noContent().build();
    }

    @Override
    public Response update(long id, IndicationDto indicationDto) {
        return IndicationDto.fromDto(indicationDto).map(i -> {
            i.setId(id); // use id from path
            indicationService.update(i);
            return Response.ok().build();
        }).orElseThrow(() -> {
            logger.debug("No indication data provided: id={}", id);
            return new IllegalArgumentException("No indication data provided");
        });
    }

    @Override
    public List<DigitDto> digits(long id) {
        return DigitDto.toDtos(indicationService.recognize(id));
    }

    @Override
    public Response digits(long id, List<DigitDto> digits) {
        indicationService.saveDigits(id, DigitDto.fromDtos(digits));
        return Response.noContent().build();
    }

    @Override
    public Response upload(Attachment attachment, Long meterId) throws Exception {
        DataHandler dataHandler = attachment.getDataHandler();
        String fileName = dataHandler.getName();
        String extension = fileName.substring(fileName.indexOf('.') + 1);

        if (Arrays.stream(this.allowedExtensions).noneMatch(x -> x.equalsIgnoreCase(extension))) {
            throw new IllegalArgumentException("Unsupported file extension detected: " + extension);
        }

        try (InputStream inputStream = dataHandler.getInputStream()) {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            Indication indication = indicationService.parseAndSaveIndication(fileName, bytes, meterId);
            return Response.status(Response.Status.CREATED)
                    .entity(IndicationDto.toDto(indication))
                    .build();
        }
    }
}
