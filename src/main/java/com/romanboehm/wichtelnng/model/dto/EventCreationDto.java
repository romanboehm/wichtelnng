package com.romanboehm.wichtelnng.model.dto;

import javax.validation.Valid;

public class EventCreationDto {
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
        return event.toString();
    }
}
