package com.romanboehm.wichtelnng.usecases.matchandnotify;

import lombok.Value;

import static java.util.Objects.requireNonNull;

@Value
public class Match {
    Donor donor;
    Recipient recipient;

    public Match(Donor donor, Recipient recipient) {
        this.donor = requireNonNull(donor);
        this.recipient = requireNonNull(recipient);
        if (donor.getParticipant().equals(recipient.getParticipant())) {
            throw new IllegalArgumentException("Donor and recipient must not match.");
        }
    }
}


