package com.asuprun.metertracker.web.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings("unused")
@Entity
@Table(name = "dashboard_item_entry")
public class DashboardItemEntry {

    @EmbeddedId
    private DashboardItemEntryKey key;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("meterId")
    @JoinColumn(name = "meter_id", referencedColumnName = "id")
    private Meter meter;

    @Column
    private int color;

    public Meter getMeter() {
        return meter;
    }

    public void setMeter(Meter meter) {
        this.meter = meter;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Embeddable
    private static class DashboardItemEntryKey implements Serializable {

        @Column(name = "meter_id")
        private long meterId;

        @Column(name = "dashboard_item_id")
        private long dashboardItemId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DashboardItemEntryKey that = (DashboardItemEntryKey) o;
            return meterId == that.meterId &&
                    dashboardItemId == that.dashboardItemId;
        }

        @Override
        public int hashCode() {

            return Objects.hash(meterId, dashboardItemId);
        }
    }
}
