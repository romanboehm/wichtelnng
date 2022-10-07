package com.romanboehm.wichtelnng.usecases.matchandnotify;

import com.romanboehm.wichtelnng.data.Event;
import lombok.Value;

@Value
class LostMailEvent {

    String title;
    String recipientEmail;
    String recipientName;

    static LostMailEvent from(Event event) {
        return new LostMailEvent(
                event.getTitle(),
                event.getHost().getEmail(),
                event.getHost().getName()
        );
    }
}
