package com.asuprun.metertracker.web.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings("unused")
@Embeddable
public class ResourceBindingId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "indication_id")
    private Indication indication;

    @Column
    @Enumerated
    private ResourceBinding.Type type;

    public ResourceBindingId() {
    }

    public ResourceBindingId(ResourceBinding.Type type, Indication indication) {
        this.type = type;
        this.indication = indication;
    }

    public Indication getIndication() {
        return indication;
    }

    public void setIndication(Indication indication) {
        this.indication = indication;
    }

    public ResourceBinding.Type getType() {
        return type;
    }

    public void setType(ResourceBinding.Type type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ResourceBindingId that = (ResourceBindingId) o;
        return indication.getId() == that.indication.getId() && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(indication.getId(), type);
    }
}
