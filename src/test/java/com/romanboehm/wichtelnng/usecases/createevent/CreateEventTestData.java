package com.romanboehm.wichtelnng.usecases.createevent;

import static com.romanboehm.wichtelnng.utils.GlobalTestData.event;

class CreateEventTestData {
    static EventForm eventForm() {
        var entity = event();
        return new EventForm()
                .setId(entity.getId())
                .setTitle(entity.getTitle())
                .setDescription(entity.getDescription())
                .setCurrency(entity.getMonetaryAmount().currency())
                .setNumber(entity.getMonetaryAmount().number())
                .setLocalDate(entity.getDeadline().localDateTime().toLocalDate())
                .setLocalTime(entity.getDeadline().localDateTime().toLocalTime())
                .setTimezone(entity.getDeadline().zoneId())
                .setHostName(entity.getHost().name())
                .setHostEmail(entity.getHost().email());
    }
}
