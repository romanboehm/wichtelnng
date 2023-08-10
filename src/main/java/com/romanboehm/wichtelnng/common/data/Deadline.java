package com.romanboehm.wichtelnng.common.data;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Embeddable
public class Deadline {

    @NotNull
    private LocalDateTime localDateTime;

    @NotNull
    private ZoneId zoneId;

    public Deadline() {
    }

    public LocalDateTime getLocalDateTime() {
        return this.localDateTime;
    }

    public Deadline setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
        return this;
    }

    public ZoneId getZoneId() {
        return this.zoneId;
    }

    public Deadline setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
        return this;
    }

    public Instant getInstant() {
        return localDateTime.atZone(zoneId).toInstant();
    }

    @Override
    public String toString() {
        return "Deadline{" +
                "localDateTime=" + localDateTime +
                ", zoneId='" + zoneId + '\'' +
                '}';
    }
}
