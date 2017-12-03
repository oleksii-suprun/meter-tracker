package com.asuprun.metertracker.web.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings("unused")
@Embeddable
public class AssetBindingId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "indication_id")
    private Indication indication;

    @Column
    @Enumerated
    private AssetBinding.Type type;

    public AssetBindingId() {
    }

    public AssetBindingId(AssetBinding.Type type, Indication indication) {
        this.type = type;
        this.indication = indication;
    }

    public Indication getIndication() {
        return indication;
    }

    public void setIndication(Indication indication) {
        this.indication = indication;
    }

    public AssetBinding.Type getType() {
        return type;
    }

    public void setType(AssetBinding.Type type) {
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

        AssetBindingId that = (AssetBindingId) o;
        return indication.getId() == that.indication.getId() && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(indication.getId(), type);
    }
}
