package com.romanboehm.wichtelnng.model;

import com.romanboehm.wichtelnng.model.entity.Participant;

public class Donor {
    private final Participant participant;

    public Donor(Participant participant) {
        this.participant = participant;
    }

    public Participant getParticipant() {
        return this.participant;
    }

    public String getName() {
        return this.participant.getName();
    }

    public String getEmail() {
        return this.participant.getEmail();
    }

    @Override
    public String toString() {
        return String.format("Donor(participant=%s)", participant);
    }
}
