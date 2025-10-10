package com.romanboehm.wichtelnng.usecases.registerparticipant;

import com.romanboehm.wichtelnng.common.data.Event;
import com.romanboehm.wichtelnng.common.data.Host;
import org.javamoney.moneta.Money;

import javax.money.MonetaryContext;
import javax.money.MonetaryContextBuilder;
import java.math.BigDecimal;
import java.util.Currency;

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
                createMoneyStringWithFixedDecimals(
                        event.getMonetaryAmount().number(),
                        event.getMonetaryAmount().currency())
        );
    }

    private static final MonetaryContext TWO_DECIMALS_MONETARY_CTX = MonetaryContextBuilder.of()
            .setMaxScale(2)
            .setFixedScale(true)
            .build();

    private static String createMoneyStringWithFixedDecimals(BigDecimal number, Currency currency) {
        return Money.of(number, currency.getCurrencyCode(), TWO_DECIMALS_MONETARY_CTX).toString();
    }

}
