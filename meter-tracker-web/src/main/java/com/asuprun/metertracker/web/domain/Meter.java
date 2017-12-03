package com.asuprun.metertracker.web.domain;

import javax.persistence.*;
import java.util.Objects;

@SuppressWarnings("unused")
@Entity
public class Meter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int capacity;

    @Column(name = "minor_digits", nullable = false)
    private int minorDigits;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String displayName) {
        this.name = displayName;
    }

    public int getMinorDigits() {
        return minorDigits;
    }

    public void setMinorDigits(int fractionalDigits) {
        this.minorDigits = fractionalDigits;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Meter meter = (Meter) o;
        return id == meter.id
                && capacity == meter.capacity
                && minorDigits == meter.minorDigits
                && Objects.equals(name, meter.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, capacity, minorDigits);
    }
}
