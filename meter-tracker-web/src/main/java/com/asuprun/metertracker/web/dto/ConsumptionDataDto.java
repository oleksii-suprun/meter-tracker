package com.asuprun.metertracker.web.dto;

import java.time.LocalDate;
import java.util.List;

public class ConsumptionDataDto {

    private long meterId;
    private List<Entry> series;

    public long getMeterId() {
        return meterId;
    }

    public void setMeterId(long meterId) {
        this.meterId = meterId;
    }

    public List<Entry> getSeries() {
        return series;
    }

    public void setSeries(List<Entry> series) {
        this.series = series;
    }

    public static class Entry {

        private LocalDate date;
        private int value;

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
