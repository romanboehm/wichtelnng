package com.romanboehm.wichtelnng.usecases.matchandnotify;

import com.romanboehm.wichtelnng.data.Event;
import com.romanboehm.wichtelnng.data.Host;
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
                Money.of(event.getMonetaryAmount().getNumber(), event.getMonetaryAmount().getCurrency()).toString()
        );
    }
}
