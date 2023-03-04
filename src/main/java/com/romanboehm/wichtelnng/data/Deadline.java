package com.romanboehm.wichtelnng.data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Embeddable
public class Deadline {

    @Column(nullable = false)
    private LocalDateTime localDateTime;

    @Column(nullable = false, length = 30)
    private String zoneId;

    public Deadline() {
    }

    public LocalDateTime getLocalDateTime() {
        return this.localDateTime;
    }

    public Deadline setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
        return this;
    }

    public String getZoneId() {
        return this.zoneId;
    }

    public Deadline setZoneId(String zoneId) {
        this.zoneId = zoneId;
        return this;
    }

    public Instant getInstant() {
        return localDateTime.atZone(ZoneId.of(zoneId)).toInstant();
    }

    @Override
    public String toString() {
        return "Deadline{" +
                "localDateTime=" + localDateTime +
                ", zoneId='" + zoneId + '\'' +
                '}';
    }
}
