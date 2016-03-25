package com.asuprun.metertracker.web.domain;

import javax.persistence.*;
import java.io.Serializable;

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
}