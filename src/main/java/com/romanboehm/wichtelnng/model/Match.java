package com.romanboehm.wichtelnng.model;

import java.util.Objects;

public class Match {
    private final Donor donor;
    private final Recipient recipient;

    public Match(Donor donor, Recipient recipient) {
        this.donor = Objects.requireNonNull(donor);
        this.recipient = Objects.requireNonNull(recipient);
        if (donor.getParticipant().equals(recipient.getParticipant())) {
            throw new IllegalArgumentException("Donor and recipient must not match.");
        }
    }

    public Donor getDonor() {
        return donor;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public String toString() {
        return String.format("Match(donor=%s, recipient=%s)", this.getDonor(), this.getRecipient());
    }
}


