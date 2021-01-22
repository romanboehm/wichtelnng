package com.romanboehm.wichtelnng.model.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ParticipantDto {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Email
    private String email;

    public String getName() {
        return name;
    }

    public ParticipantDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public ParticipantDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public String toString() {
        return String.format("Participant(name=%s, email=%s)", this.getName(), this.getEmail());
    }
}
