package com.romanboehm.wichtelnng.usecases.notify;

import com.romanboehm.wichtelnng.common.data.Event;
import com.romanboehm.wichtelnng.common.data.Host;

record MailToParticipantDataForCancelledEvent(String recipientEmail, String recipientName, String title, Host host) {

    static MailToParticipantDataForCancelledEvent from(Event event, String recipientName, String recipientEmail) {
        return new MailToParticipantDataForCancelledEvent(recipientEmail, recipientName, event.getTitle(), event.getHost());
    }
}
