package com.romanboehm.wichtelnng.usecases.createevent;

import com.romanboehm.wichtelnng.data.Event;

import javax.money.Monetary;
import java.time.ZoneId;

import static com.romanboehm.wichtelnng.GlobalTestData.event;

class CreateEventTestData {
    static CreateEvent createEvent() {
        Event entity = event();
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
