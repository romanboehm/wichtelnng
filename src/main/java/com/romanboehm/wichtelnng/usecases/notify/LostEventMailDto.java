package com.romanboehm.wichtelnng.usecases.notify;

import com.romanboehm.wichtelnng.common.data.Event;

record LostEventMailDto(String title, String recipientEmail, String recipientName) {

    static LostEventMailDto from(Event event) {
        return new LostEventMailDto(
                event.getTitle(),
                event.getHost().email(),
                event.getHost().name()
        );
    }
}
