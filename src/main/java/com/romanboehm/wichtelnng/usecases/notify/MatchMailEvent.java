package com.romanboehm.wichtelnng.usecases.notify;

import com.romanboehm.wichtelnng.common.data.Event;
import com.romanboehm.wichtelnng.common.data.Host;
import org.javamoney.moneta.Money;

record MatchMailEvent(
        Donor donor,
        Recipient recipient,
        String title,
        String description,
        Host host,
        String price
) {

    static MatchMailEvent from(Event event, Donor donor, Recipient recipient) {
        return new MatchMailEvent(
                donor,
                recipient,
                event.getTitle(),
                event.getDescription(),
                event.getHost(),
                Money.of(event.getMonetaryAmount().number(), event.getMonetaryAmount().currency().getCurrencyCode()).toString()
        );
    }
}
