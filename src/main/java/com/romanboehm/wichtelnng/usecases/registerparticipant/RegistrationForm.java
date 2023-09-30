package com.romanboehm.wichtelnng.usecases.registerparticipant;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Class may be package-private, but properties (i.e. getters) need be public for validator.
class RegistrationForm {

    @NotBlank
    @Size(max = 100)
    private String participantName;

    @NotBlank
    @Email
    private String participantEmail;

    public String getParticipantName() {
        return participantName;
    }

    public RegistrationForm setParticipantName(String participantName) {
        this.participantName = participantName;
        return this;
    }

    public String getParticipantEmail() {
        return participantEmail;
    }

    public RegistrationForm setParticipantEmail(String participantEmail) {
        this.participantEmail = participantEmail;
        return this;
    }

    @Override
    public String toString() {
        return "RegisterParticipant{" +
                "participantName='" + participantName + '\'' +
                ", participantEmail='" + participantEmail + '\'' +
                '}';
    }
}
