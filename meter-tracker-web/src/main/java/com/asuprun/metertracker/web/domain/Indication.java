package com.asuprun.metertracker.web.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Entity
public class Indication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private Double value;

    @OneToMany(mappedBy = "resourceBindingId.indication", fetch = FetchType.EAGER, orphanRemoval = true)
    @MapKey(name = "resourceBindingId.type")
    private Map<ResourceBinding.Type, ResourceBinding> images;

    @Column(nullable = false, updatable = false)
    private Date uploaded;

    @Column
    private Date created;

    @ManyToOne(fetch = FetchType.EAGER)
    private Meter meter;

    @JsonIgnore
    @Column(nullable = false, unique = true, updatable = false)
    private String hash;

    @Column
    @Min(0)
    private Integer consumption;

    public Indication() {
        this.images = new HashMap<>();
    }

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

    public Map<ResourceBinding.Type, ResourceBinding> getImages() {
        return images;
    }

    public void setImages(Map<ResourceBinding.Type, ResourceBinding> images) {
        this.images = images;
    }

    public Date getUploaded() {
        return uploaded;
    }

    public void setUploaded(Date uploadDate) {
        this.uploaded = uploadDate;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date createdDate) {
        this.created = createdDate;
    }

    public Meter getMeter() {
        return meter;
    }

    public void setMeter(Meter meter) {
        this.meter = meter;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Integer getConsumption() {
        return consumption;
    }

    public void setConsumption(Integer consumption) {
        this.consumption = consumption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Indication that = (Indication) o;

        Set<Long> thisAssetIds = images.values().stream()
                .map(ResourceBinding::getResource).map(Resource::getId).collect(Collectors.toSet());

        Set<Long> thatAssetIds = that.images.values().stream()
                .map(ResourceBinding::getResource).map(Resource::getId).collect(Collectors.toSet());

        return id == that.id
                && Objects.equals(value, that.value)
                && Objects.equals(thisAssetIds, thatAssetIds)
                && Objects.equals(uploaded, that.uploaded)
                && Objects.equals(created, that.created)
                && Objects.equals(meter, that.meter)
                && Objects.equals(hash, that.hash)
                && Objects.equals(consumption, that.consumption);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, images, uploaded, created, meter, hash, consumption);
    }
}
