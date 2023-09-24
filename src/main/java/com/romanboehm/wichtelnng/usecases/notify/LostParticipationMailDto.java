package com.romanboehm.wichtelnng.usecases.notify;

import com.romanboehm.wichtelnng.common.data.Event;
import com.romanboehm.wichtelnng.common.data.Host;

record LostParticipationMailDto(String recipientEmail, String recipientName, String title, Host host) {

    static LostParticipationMailDto from(Event event, String recipientName, String recipientEmail) {
        return new LostParticipationMailDto(recipientEmail, recipientName, event.getTitle(), event.getHost());
    }
}
