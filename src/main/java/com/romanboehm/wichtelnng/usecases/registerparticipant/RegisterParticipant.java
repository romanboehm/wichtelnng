package com.romanboehm.wichtelnng.usecases.registerparticipant;

import com.romanboehm.wichtelnng.common.data.Event;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

// Class may be package-private, but properties (i.e. getters) need be public for validator.
class RegisterParticipant {

    @NotNull
    private UUID id;

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    @Size(max = 1000)
    private String description;

    @NotNull
    @Min(0)
    private BigDecimal number;

    @NotNull
    private Currency currency;

    @NotBlank
    @Size(max = 100)
    private String hostName;

    @NotBlank
    @Email
    private String hostEmail;

    @NotBlank
    @Size(max = 100)
    private String participantName;

    @NotBlank
    @Email
    private String participantEmail;

    static RegisterParticipant registerFor(Event event) {
        return new RegisterParticipant()
                .setId(event.getId())
                .setTitle(event.getTitle())
                .setDescription(event.getDescription())
                .setCurrency(event.getMonetaryAmount().getCurrency())
                .setNumber(event.getMonetaryAmount().getNumber())
                .setHostName(event.getHost().getName())
                .setHostEmail(event.getHost().getEmail());
    }

    public UUID getId() {
        return id;
    }

    public RegisterParticipant setId(UUID id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public RegisterParticipant setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public RegisterParticipant setDescription(String description) {
        this.description = description;
        return this;
    }

    public BigDecimal getNumber() {
        return number;
    }

    public RegisterParticipant setNumber(BigDecimal number) {
        this.number = number;
        return this;
    }

    public Currency getCurrency() {
        return currency;
    }

    public RegisterParticipant setCurrency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public String getHostName() {
        return hostName;
    }

    public RegisterParticipant setHostName(String hostName) {
        this.hostName = hostName;
        return this;
    }

    public String getHostEmail() {
        return hostEmail;
    }

    public RegisterParticipant setHostEmail(String hostEmail) {
        this.hostEmail = hostEmail;
        return this;
    }

    public String getParticipantName() {
        return participantName;
    }

    public RegisterParticipant setParticipantName(String participantName) {
        this.participantName = participantName;
        return this;
    }

    public String getParticipantEmail() {
        return participantEmail;
    }

    public RegisterParticipant setParticipantEmail(String participantEmail) {
        this.participantEmail = participantEmail;
        return this;
    }

    @Override
    public String toString() {
        return "RegisterParticipant{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", number=" + number +
                ", currency=" + currency +
                ", hostName='" + hostName + '\'' +
                ", hostEmail='" + hostEmail + '\'' +
                ", participantName='" + participantName + '\'' +
                ", participantEmail='" + participantEmail + '\'' +
                '}';
    }
}
