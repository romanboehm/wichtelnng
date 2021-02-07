package com.romanboehm.wichtelnng.model;

import com.romanboehm.wichtelnng.model.entity.Participant;

public class Recipient {
    private final Participant participant;

    public Recipient(Participant participant) {
        this.participant = participant;
    }

    public Participant getParticipant() {
        return this.participant;
    }

    public String getName() {
        return this.participant.getName();
    }

    @Override
    public String toString() {
        return String.format("Recipient(participant=%s)", participant);
    }
}
