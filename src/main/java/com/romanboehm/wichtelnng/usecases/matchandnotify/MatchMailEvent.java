package com.romanboehm.wichtelnng.usecases.matchandnotify;

import com.romanboehm.wichtelnng.data.Event;
import com.romanboehm.wichtelnng.data.Host;
import com.romanboehm.wichtelnng.data.MonetaryAmount;
import lombok.Value;

@Value
class MatchMailEvent {

    Donor donor;
    Recipient recipient;
    String title;
    String description;
    Host host;
    MonetaryAmount monetaryAmount;

    static MatchMailEvent from(Event event, Donor donor, Recipient recipient) {
        return new MatchMailEvent(
                donor,
                recipient,
                event.getTitle(),
                event.getDescription(),
                event.getHost(),
                event.getMonetaryAmount()
        );
    }
}
