package com.romanboehm.wichtelnng.model.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ParticipantRegistration {

    @NotNull
    @Valid
    private EventDto event;

    @NotNull
    @Valid
    private ParticipantDto participant;

    public ParticipantRegistration(EventDto event) {
        this.event = event;
    }

    public EventDto getEvent() {
        return event;
    }

    public ParticipantRegistration setEvent(EventDto event) {
        this.event = event;
        return this;
    }

    public ParticipantDto getParticipant() {
        return participant;
    }

    public ParticipantRegistration setParticipant(ParticipantDto participant) {
        this.participant = participant;
        return this;
    }

    @Override
    public String toString() {
        return String.format(
                "ParticipantRegistration(event=%s, participant=%s)",
                event != null ? event : "",
                participant != null ? participant : ""
        );
    }
}
