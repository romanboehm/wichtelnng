package com.romanboehm.wichtelnng.data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.annotations.Formula;

import java.time.Instant;
import java.time.LocalDateTime;

@Embeddable
public class Deadline {

    @Column(nullable = false)
    private LocalDateTime localDateTime;

    @Column(nullable = false, length = 30)
    private String zoneId;

    @Formula("timezone(zone_id, local_date_time)")
    private Instant instant;

    public Deadline() {
    }

    public LocalDateTime getLocalDateTime() {
        return this.localDateTime;
    }

    public String getZoneId() {
        return this.zoneId;
    }

    public Instant getInstant() {
        return this.instant;
    }

    public Deadline setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
        return this;
    }

    public Deadline setZoneId(String zoneId) {
        this.zoneId = zoneId;
        return this;
    }

    @Override
    public String toString() {
        return "Deadline{" +
                "localDateTime=" + localDateTime +
                ", zoneId='" + zoneId + '\'' +
                ", instant=" + instant +
                '}';
    }
}
