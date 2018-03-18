package com.asuprun.metertracker.web.domain;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.Date;
import java.util.Objects;

@SuppressWarnings("unused")
@Entity
public class Indication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private Double value;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "original_image_info_id")
    private ImageInfo originalImageInfo;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "indication_image_info_id")
    private ImageInfo indicationImageInfo;

    @Column(name = "created_at")
    private Date createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "meter_id")
    private Meter meter;

    @Column
    @Min(0)
    private Integer consumption;

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

    public ImageInfo getOriginalImageInfo() {
        return originalImageInfo;
    }

    public void setOriginalImageInfo(ImageInfo originalImageInfo) {
        this.originalImageInfo = originalImageInfo;
    }

    public ImageInfo getIndicationImageInfo() {
        return indicationImageInfo;
    }

    public void setIndicationImageInfo(ImageInfo indicationImageInfo) {
        this.indicationImageInfo = indicationImageInfo;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Meter getMeter() {
        return meter;
    }

    public void setMeter(Meter meter) {
        this.meter = meter;
    }

    public Integer getConsumption() {
        return consumption;
    }

    public void setConsumption(Integer consumption) {
        this.consumption = consumption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Indication that = (Indication) o;
        return id == that.id &&
                Objects.equals(value, that.value) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(consumption, that.consumption);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, createdAt, consumption);
    }
}
