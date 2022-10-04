package com.romanboehm.wichtelnng.data;

import lombok.Data;
import org.hibernate.annotations.Formula;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Embeddable
public class Deadline {

    @Column(nullable = false)
    private LocalDateTime localDateTime;

    @Column(nullable = false, length = 30)
    private String zoneId;

    @Formula("timezone(zone_id, local_date_time)")
    private Instant instant;
}
