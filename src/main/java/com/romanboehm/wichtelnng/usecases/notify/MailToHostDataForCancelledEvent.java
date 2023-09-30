package com.romanboehm.wichtelnng.usecases.notify;

import com.romanboehm.wichtelnng.common.data.Event;

record MailToHostDataForCancelledEvent(String title, String recipientEmail, String recipientName) {

    static MailToHostDataForCancelledEvent from(Event event) {
        return new MailToHostDataForCancelledEvent(
                event.getTitle(),
                event.getHost().email(),
                event.getHost().name()
        );
    }
}
