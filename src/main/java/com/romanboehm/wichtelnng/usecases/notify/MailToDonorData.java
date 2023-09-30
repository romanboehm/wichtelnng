package com.romanboehm.wichtelnng.usecases.notify;

import com.romanboehm.wichtelnng.common.data.Event;
import com.romanboehm.wichtelnng.common.data.Host;
import org.javamoney.moneta.Money;

record MailToDonorData(
        Match match,
        String title,
        String description,
        Host host,
        String price
) {

    static MailToDonorData from(Event event, Match match) {
        return new MailToDonorData(
                match,
                event.getTitle(),
                event.getDescription(),
                event.getHost(),
                Money.of(event.getMonetaryAmount().number(), event.getMonetaryAmount().currency().getCurrencyCode()).toString()
        );
    }
}
