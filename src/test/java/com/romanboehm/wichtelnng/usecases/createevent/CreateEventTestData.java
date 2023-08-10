package com.romanboehm.wichtelnng.usecases.createevent;

import java.time.ZoneId;

import static com.romanboehm.wichtelnng.utils.GlobalTestData.event;

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
                .setCurrency(entity.getMonetaryAmount().getCurrency())
                .setNumber(entity.getMonetaryAmount().getNumber())
                .setLocalDate(entityDeadlineZdt.toLocalDate())
                .setLocalTime(entityDeadlineZdt.toLocalTime())
                .setTimezone(entityDeadlineZdt.getZone())
                .setHostName(entity.getHost().getName())
                .setHostEmail(entity.getHost().getEmail());
    }
}
