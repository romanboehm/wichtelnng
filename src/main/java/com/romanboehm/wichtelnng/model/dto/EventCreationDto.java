package com.romanboehm.wichtelnng.model.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class EventCreationDto {
    @NotNull
    @Valid
    private EventDto event;

    public EventCreationDto(EventDto event) {
        this.event = event;
    }

    public EventDto getEvent() {
        return event;
    }

    public EventCreationDto setEvent(EventDto event) {
        this.event = event;
        return this;
    }

    @Override
    public String toString() {
        return String.format("EventCreation(event=%s)", event != null ? event : "");
    }
}
