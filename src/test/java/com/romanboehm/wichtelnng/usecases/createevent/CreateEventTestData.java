package com.romanboehm.wichtelnng.usecases.createevent;

import javax.money.Monetary;
import java.time.ZoneId;

import static com.romanboehm.wichtelnng.GlobalTestData.event;

class CreateEventTestData {
    static CreateEvent createEvent() {
        var entity = event();
        var entityDeadlineZdt = entity.getDeadline()
                .getInstant()
                .atZone(ZoneId.of(entity.getDeadline().getZoneId()));
        return new CreateEvent()
                .setId(entity.getId())
                .setTitle(entity.getTitle())
                .setDescription(entity.getDescription())
                .setCurrency(Monetary.getCurrency(entity.getMonetaryAmount().getCurrency()))
                .setNumber(entity.getMonetaryAmount().getNumber())
                .setLocalDate(entityDeadlineZdt.toLocalDate())
                .setLocalTime(entityDeadlineZdt.toLocalTime())
                .setTimezone(entityDeadlineZdt.getZone())
                .setHostName(entity.getHost().getName())
                .setHostEmail(entity.getHost().getEmail());
    }
}
