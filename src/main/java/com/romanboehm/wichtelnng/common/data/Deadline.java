package com.romanboehm.wichtelnng.common.data;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Embeddable
public record Deadline(
    @NotNull
    LocalDateTime localDateTime,
    @NotNull
    ZoneId zoneId
) {}