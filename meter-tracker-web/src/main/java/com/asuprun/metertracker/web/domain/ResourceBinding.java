package com.asuprun.metertracker.web.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.util.Objects;

@SuppressWarnings("unused")
@Entity(name = "resource_binding")
public class ResourceBinding {

    public enum Type {
        ORIGINAL,
        INDICATION
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ResourceBinding that = (ResourceBinding) o;
        return Objects.equals(resourceBindingId, that.resourceBindingId)
                && resource.getId() == that.resource.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceBindingId, resource.getId());
    }
}
