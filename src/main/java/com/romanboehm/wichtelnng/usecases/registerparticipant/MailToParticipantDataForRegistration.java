package com.romanboehm.wichtelnng.usecases.registerparticipant;

import com.romanboehm.wichtelnng.common.data.Event;
import com.romanboehm.wichtelnng.common.data.Host;
import org.javamoney.moneta.Money;

record MailToParticipantDataForRegistration(
        String title,
        String description,
        String participantName,
        String participantEmail,
        Host host,
        String price
) {
    static MailToParticipantDataForRegistration from(Event event, RegistrationForm registrationForm) {
        return new MailToParticipantDataForRegistration(
                event.getTitle(),
                event.getDescription(),
                registrationForm.getParticipantName(),
                registrationForm.getParticipantEmail(),
                event.getHost(),
                Money.of(event.getMonetaryAmount().number(), event.getMonetaryAmount().currency().getCurrencyCode()).toString()
        );
    }

}
