package com.romanboehm.wichtelnng.usecases.createevent;

import com.romanboehm.wichtelnng.GlobalTestData;
import com.romanboehm.wichtelnng.data.Event;

import javax.money.Monetary;
import java.time.ZoneId;

class CreateEventTestData {
    static CreateEvent createEvent() {
        Event entity = GlobalTestData.event();
        return new CreateEvent()
                .setId(entity.getId())
                .setTitle(entity.getTitle())
                .setDescription(entity.getDescription())
                .setCurrency(Monetary.getCurrency(entity.getMonetaryAmount().getCurrency()))
                .setNumber(entity.getMonetaryAmount().getNumber())
                .setLocalDate(entity.getDeadline().getLocalDateTime().atZone(ZoneId.of(entity.getDeadline().getZoneId())).toLocalDate())
                .setLocalTime(entity.getDeadline().getLocalDateTime().atZone(ZoneId.of(entity.getDeadline().getZoneId())).toLocalTime())
                .setTimezone(ZoneId.of(entity.getDeadline().getZoneId()))
                .setHostName(entity.getHost().getName())
                .setHostEmail(entity.getHost().getEmail());
    }
}
