package com.romanboehm.wichtelnng.usecases.createevent;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

import static java.time.ZoneId.getAvailableZoneIds;
import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;

// Class may be package-private, but properties (i.e. getters) need be public for validator.
class EventForm {

    private static final List<Currency> CURRENCIES = Currency.getAvailableCurrencies().stream()
            .sorted(Comparator.comparing(Currency::getCurrencyCode))
            .toList();

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
    private Currency currency;
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
    @NotBlank
    @Size(max = 100)
    private String hostName;
    @NotBlank
    @Email
    private String hostEmail;

    // Needed to delegate validation for event's "when" (its local date and local time at the respective timezone) to
    // the javax validator.
    // Non-nullability of the date, time, and timezone components is validated separately through field annotations.
    @FutureOrPresent
    public Instant getInstant() {
        var localDateTime = (localDate != null && localTime != null) ? LocalDateTime.of(localDate, localTime) : LocalDateTime.now();
        var zone = timezone != null ? timezone : ZoneId.systemDefault();
        return ZonedDateTime.of(localDateTime, zone).toInstant();
    }

    public List<Currency> getCurrencies() {
        return CURRENCIES;
    }

    public List<EventZoneId> getTimezones() {
        return EventZoneId.ALL_ZONES;
    }

    public UUID getId() {
        return id;
    }

    public EventForm setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public EventForm setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public EventForm setDescription(String description) {
        this.description = description;
        return this;
    }

    public BigDecimal getNumber() {
        return number;
    }

    public EventForm setNumber(BigDecimal number) {
        this.number = number;
        return this;
    }

    public Currency getCurrency() {
        return currency;
    }

    public EventForm setCurrency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public EventForm setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
        return this;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public EventForm setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
        return this;
    }

    public ZoneId getTimezone() {
        return timezone;
    }

    public EventForm setTimezone(ZoneId timezone) {
        this.timezone = timezone;
        return this;
    }

    public String getHostName() {
        return hostName;
    }

    public EventForm setHostName(String hostName) {
        this.hostName = hostName;
        return this;
    }

    public String getHostEmail() {
        return hostEmail;
    }

    public EventForm setHostEmail(String hostEmail) {
        this.hostEmail = hostEmail;
        return this;
    }

    @Override
    public String toString() {
        return "CreateEvent{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", number=" + number +
                ", currency=" + currency +
                ", localDate=" + localDate +
                ", localTime=" + localTime +
                ", timezone=" + timezone +
                ", hostName='" + hostName + '\'' +
                ", hostEmail='" + hostEmail + '\'' +
                '}';
    }

    record EventZoneId(ZoneId zoneId) {
        private static final LocalDateTime NOW = LocalDateTime.now();

        private static final Comparator<EventZoneId> COMPARATOR = comparing(
                eventZoneId -> NOW.atZone(eventZoneId.zoneId()).getOffset(), reverseOrder()
        );
        private static final List<EventZoneId> ALL_ZONES = getAvailableZoneIds().stream()
                .map(ZoneId::of)
                .map(EventZoneId::new)
                .sorted(COMPARATOR)
                .collect(toList());

        public String getDisplayString() {
            return "%s (UTC %s)".formatted(
                    zoneId.getId(),
                    NOW
                            .atZone(zoneId)
                            .getOffset()
                            .getId()
                            .replace("Z", "+00:00")
            );
        }

    }

}
