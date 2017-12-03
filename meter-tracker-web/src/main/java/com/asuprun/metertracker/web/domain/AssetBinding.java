package com.asuprun.metertracker.web.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.util.Objects;

@SuppressWarnings("unused")
@Entity(name = "indication_asset_binding")
public class AssetBinding {

    public enum Type {
        ORIGINAL,
        INDICATION
    }

    @JsonIgnore
    @EmbeddedId
    private AssetBindingId assetBindingId;

    @OneToOne(orphanRemoval = true)
    private Asset asset;

    public AssetBindingId getAssetBindingId() {
        return assetBindingId;
    }

    public void setAssetBindingId(AssetBindingId id) {
        this.assetBindingId = id;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssetBinding that = (AssetBinding) o;
        return Objects.equals(assetBindingId, that.assetBindingId) && asset.getId() == that.asset.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(assetBindingId, asset.getId());
    }
}
