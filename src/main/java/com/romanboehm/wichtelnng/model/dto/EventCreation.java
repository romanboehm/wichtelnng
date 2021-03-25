package com.romanboehm.wichtelnng.model.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.money.CurrencyUnit;
import javax.validation.constraints.Email;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;


@Data
public class EventCreation {

    // May be `null` first
    private UUID id;

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    @Size(max = 1000)
    private String description;

    @NotNull
    @Min(0)
    private BigDecimal number;

    @NotNull
    private CurrencyUnit currency;


    // Keep date and time apart since we replaced `input[@type='datetime-local']` with two separate `inputs` for reasons
    // of browser compatibility and ease of use.
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate localDate;

    // Keep date and time apart since we replaced `input[@type='datetime-local']` with two separate `inputs` for reasons
    // of browser compatibility and ease of use.
    @NotNull
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime localTime;

    @NotNull
    private ZoneId timezone;

    // Needed to delegate validation for event's "when" (its local date and local time at the respective timezone) to
    // the javax validator.
    // Non-nullability of the date, time, and timezone components is validated separately through field annotations.
    @FutureOrPresent
    public Instant getInstant() {
        return ZonedDateTime.of(
                localDate != null ? localDate : LocalDate.now(),
                localTime != null ? localTime : LocalTime.now(),
                timezone != null ? timezone : ZoneId.systemDefault()
        ).toInstant();
    }

    @NotBlank
    @Size(max = 100)
    private String hostName;

    @NotBlank
    @Email
    private String hostEmail;
}
