package com.romanboehm.wichtelnng.usecases.notify;

import com.romanboehm.wichtelnng.common.data.Event;

record LostMailEvent(String title, String recipientEmail, String recipientName) {

    static LostMailEvent from(Event event) {
        return new LostMailEvent(
                event.getTitle(),
                event.getHost().getEmail(),
                event.getHost().getName()
        );
    }
}
