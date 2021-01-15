package com.romanboehm.wichtelnng.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.money.Monetary;
import javax.validation.Valid;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Event {

    public static Event withMinimalDefaults() {
        Event event = new Event();
        MonetaryAmount monetaryAmount = new MonetaryAmount();
        monetaryAmount.setCurrency(Monetary.getCurrency("EUR")); // set default currency
        event.setMonetaryAmount(monetaryAmount);
        return event;
    }

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    @Size(max = 1000)
    private String description;

    @NotNull
    @Valid
    private MonetaryAmount monetaryAmount;

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

    @NotBlank
    @Size(max = 100)
    private String place;

    @NotNull
    @Valid
    private Host host;

    @NotNull
    private List<@Valid Participant> participants;

    public Event() {
        participants = new ArrayList<>();
    }

    public void addParticipant(Participant participant) {
        participants.add(participant);
    }

    private void removeParticipant(Participant participant) {
        participants.remove(participant);
    }

    public void removeParticipantNumber(int index) {
        removeParticipant(participants.get(index));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MonetaryAmount getMonetaryAmount() {
        return monetaryAmount;
    }

    public void setMonetaryAmount(MonetaryAmount monetaryAmount) {
        this.monetaryAmount = monetaryAmount;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    // Needed to delegate validation for event's "when" (its local date at its local time) to the javax validator
    // Non-nullability of the date resp. time components is validated separately through field annotations.
    @FutureOrPresent
    public LocalDateTime getLocalDateTime() {
        return LocalDateTime.of(
                localDate != null ? localDate : LocalDate.now(),
                localTime != null ? localTime : LocalTime.now()
        );
    }

    public String toString() {
        return String.format(
                "Event(title=%s, description=%s, monetaryAmount=%s, localDate=%s, localTime=%s, place=%s, host=%s, participants=%s)",
                this.getTitle(),
                this.getDescription(),
                this.getMonetaryAmount(),
                this.getLocalDate(),
                this.getLocalTime(),
                this.getPlace(),
                this.getHost(),
                this.getParticipants()
        );
    }

}
