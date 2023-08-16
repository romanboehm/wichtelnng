package com.romanboehm.wichtelnng.common.data;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.Length;

import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ParticipantId {
    @NaturalId
    @NotBlank
    @Length(max = 100)
    private String name;

    @NaturalId
    @NotBlank
    @Length(max = 255)
    private String email;

    @NotNull
    private UUID eventId;

    public String getName() {
        return name;
    }

    public ParticipantId setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public ParticipantId setEmail(String email) {
        this.email = email;
        return this;
    }

    public UUID getEventId() {
        return eventId;
    }

    public ParticipantId setEventId(UUID eventId) {
        this.eventId = eventId;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        ParticipantId that = (ParticipantId) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.email, that.email) &&
                Objects.equals(this.eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, eventId);
    }

    @Override
    public String toString() {
        return "ParticipantId[" +
                "name=" + name + ", " +
                "email=" + email + ", " +
                "eventId=" + eventId + ']';
    }

}
