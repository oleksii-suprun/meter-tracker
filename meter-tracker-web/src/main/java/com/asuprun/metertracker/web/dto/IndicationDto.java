package com.asuprun.metertracker.web.dto;

import com.asuprun.metertracker.web.domain.Indication;
import com.asuprun.metertracker.web.domain.ResourceBinding;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class IndicationDto {

    private long id;
    private Double value;
    private long originalImageId;
    private long indicationImageId;
    private Date uploaded;
    private Date created;
    private String meterName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public long getOriginalImageId() {
        return originalImageId;
    }

    public void setOriginalImageId(long originalImageId) {
        this.originalImageId = originalImageId;
    }

    public long getIndicationImageId() {
        return indicationImageId;
    }

    public void setIndicationImageId(long indicationImageId) {
        this.indicationImageId = indicationImageId;
    }

    public Date getUploaded() {
        return uploaded;
    }

    public void setUploaded(Date uploaded) {
        this.uploaded = uploaded;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getMeterName() {
        return meterName;
    }

    public void setMeterName(String meterName) {
        this.meterName = meterName;
    }

    public static IndicationDto toDto(Indication indication) {
        if (indication == null) {
            return null;
        }
        IndicationDto dto = new IndicationDto();
        dto.id = indication.getId();
        dto.value = indication.getValue();
        dto.originalImageId = indication.getImages().get(ResourceBinding.Type.ORIGINAL).getResource().getId();
        dto.indicationImageId = indication.getImages().get(ResourceBinding.Type.INDICATION).getResource().getId();
        dto.uploaded = indication.getUploaded();
        dto.created = indication.getCreated();
        dto.meterName = indication.getMeter().getName();
        return dto;
    }

    public static List<IndicationDto> toDtos(List<Indication> indications) {
        return indications.stream().map(IndicationDto::toDto).collect(Collectors.toList());
    }
}
