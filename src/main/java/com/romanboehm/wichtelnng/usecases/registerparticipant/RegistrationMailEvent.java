package com.romanboehm.wichtelnng.usecases.registerparticipant;

import com.romanboehm.wichtelnng.common.data.Event;
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
    static RegistrationMailEvent from(Event event, RegisterParticipant registerParticipant) {
        return new RegistrationMailEvent(
                event.getTitle(),
                event.getDescription(),
                registerParticipant.getParticipantName(),
                registerParticipant.getParticipantEmail(),
                event.getHost().name(),
                event.getHost().email(),
                Money.of(event.getMonetaryAmount().number(), event.getMonetaryAmount().currency().getCurrencyCode()).toString()
        );
    }

}
