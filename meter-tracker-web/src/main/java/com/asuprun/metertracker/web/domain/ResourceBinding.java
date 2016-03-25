package com.asuprun.metertracker.web.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@SuppressWarnings("unused")
@Entity(name = "resource_binding")
public class ResourceBinding {

    @JsonIgnore
    @EmbeddedId
    private ResourceBindingId resourceBindingId;

    @OneToOne(orphanRemoval = true)
    private Resource resource;

    public ResourceBindingId getResourceBindingId() {
        return resourceBindingId;
    }

    public void setResourceBindingId(ResourceBindingId id) {
        this.resourceBindingId = id;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public enum Type {
        ORIGINAL,
        INDICATION
    }
}
