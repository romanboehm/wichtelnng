package com.romanboehm.wichtelnng.model.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ParticipantsMatch {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParticipantsMatch.class);
    private final Donor donor;
    private final Recipient recipient;

    public ParticipantsMatch(Donor donor, Recipient recipient) {
        this.donor = Objects.requireNonNull(donor);
        this.recipient = Objects.requireNonNull(recipient);
        if (donor.getParticipant().equals(recipient.getParticipant())) {
            LOGGER.error("Tried to match donor {} with recipient {}", donor, recipient);
            throw new IllegalArgumentException("Donor and recipient must not match");
        }
    }

    public Donor getDonor() {
        return donor;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public String toString() {
        return String.format("ParticipantsMatch(donor=%s, recipient=%s)", this.getDonor(), this.getRecipient());
    }


    public static class Recipient {
        private final ParticipantDto participant;

        public Recipient(ParticipantDto participant) {
            this.participant = participant;
        }

        public ParticipantDto getParticipant() {
            return this.participant;
        }

        public String getName() {
            return this.participant.getName();
        }

        @Override
        public String toString() {
            return participant.toString();
        }
    }

    public static class Donor {
        private final ParticipantDto participant;

        public Donor(ParticipantDto participant) {
            this.participant = participant;
        }

        public ParticipantDto getParticipant() {
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
            return participant.toString();
        }
    }
}


