package com.romanboehm.wichtelnng.common.data;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Embeddable
public record Deadline(
    @NotNull
    LocalDateTime localDateTime,
    @NotNull
    ZoneId zoneId
) {

    @Transient
    public Instant asInstant() {
        return localDateTime.atZone(zoneId).toInstant();
    }
}