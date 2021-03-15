package com.romanboehm.wichtelnng.model;

import lombok.Value;

import java.util.Objects;

@Value
public class Match {
    Donor donor;
    Recipient recipient;

    public Match(Donor donor, Recipient recipient) {
        this.donor = Objects.requireNonNull(donor);
        this.recipient = Objects.requireNonNull(recipient);
        if (donor.getParticipant().equals(recipient.getParticipant())) {
            throw new IllegalArgumentException("Donor and recipient must not match.");
        }
    }
}


