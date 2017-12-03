package com.asuprun.metertracker.web.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Objects;

@SuppressWarnings("unused")
@Entity
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @Lob
    @Column(nullable = false, updatable = false)
    private byte[] data;

    public Resource() {
    }

    public Resource(byte[] data) {
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Resource resource = (Resource) o;
        return id == resource.id && Arrays.equals(data, resource.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, data);
    }
}
