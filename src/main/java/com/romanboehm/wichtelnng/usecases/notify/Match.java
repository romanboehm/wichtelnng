package com.romanboehm.wichtelnng.usecases.notify;

record Match(Donor donor, Recipient recipient) {

    Match {
        if (donor.name().equals(recipient.name()) && donor.email().equals(recipient.email())) {
            throw new IllegalArgumentException("Donor and recipient must not match.");
        }
    }

    record Donor(String name, String email) {
    }

    record Recipient(String name, String email) {
    }
}
