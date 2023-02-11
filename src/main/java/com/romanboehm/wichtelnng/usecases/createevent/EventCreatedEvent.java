package com.romanboehm.wichtelnng.usecases.createevent;

import org.springframework.context.ApplicationEvent;

import java.time.Instant;
import java.util.UUID;

public class EventCreatedEvent extends ApplicationEvent {

    private final UUID eventId;
    private final Instant eventDeadline;

    public EventCreatedEvent(Object source, UUID eventId, Instant eventDeadline) {
        super(source);
        this.eventId = eventId;
        this.eventDeadline = eventDeadline;
    }

    public UUID getEventId() {
        return eventId;
    }

    public Instant getEventDeadline() {
        return eventDeadline;
    }

    @Override
    public String toString() {
        return "EventCreatedEvent{" +
                "eventId=" + eventId +
                ", eventDeadline=" + eventDeadline +
                ", source=" + source +
                '}';
    }
}
