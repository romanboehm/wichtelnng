package com.romanboehm.wichtelnng.data;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
@Embeddable
public class Deadline {

    @Column(nullable = false)
    private LocalDateTime localDateTime;

    @Column(nullable = false, length = 30)
    private String zoneId;

    public Instant asInstant() {
        return ZonedDateTime.of(
                getLocalDateTime(),
                ZoneId.of(getZoneId())
        ).toInstant();
    }
}
