package com.asuprun.metertracker.web.domain;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Objects;

@Entity
public class Digit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob
    @Column(nullable = false)
    private byte[] image;

    @Column
    private String value;

    public Digit() {
    }

    public Digit(byte[] image, String value) {
        this.image = image;
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Digit digit = (Digit) o;
        return id == digit.id
                && Arrays.equals(image, digit.image)
                && Objects.equals(value, digit.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, image, value);
    }
}
