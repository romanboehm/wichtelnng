package com.romanboehm.wichtelnng.usecases.registerparticipant;

import org.javamoney.moneta.Money;

record RegistrationMailEvent(
        String title,
        String description,
        String participantName,
        String participantEmail,
        String hostName,
        String hostEmail,
        String price
) {
    static RegistrationMailEvent from(RegisterParticipant registerParticipant) {
        return new RegistrationMailEvent(
                registerParticipant.getTitle(),
                registerParticipant.getDescription(),
                registerParticipant.getParticipantName(),
                registerParticipant.getParticipantEmail(),
                registerParticipant.getHostName(),
                registerParticipant.getHostEmail(),
                Money.of(registerParticipant.getNumber(), registerParticipant.getCurrency()).toString()
        );
    }

}
