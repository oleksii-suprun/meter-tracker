package com.asuprun.metertracker.web.domain;

import javax.persistence.*;

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
}
