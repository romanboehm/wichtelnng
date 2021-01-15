package com.romanboehm.wichtelnng.model.util;

import com.romanboehm.wichtelnng.model.dto.EventDto;
import com.romanboehm.wichtelnng.model.entity.Event;
import com.romanboehm.wichtelnng.model.entity.Host;
import com.romanboehm.wichtelnng.model.entity.MonetaryAmount;

import java.util.UUID;

public class EventBuilder {
    public static Event from(EventDto dto) {
        Event entity = new Event()
                .setId(UUID.randomUUID())
                .setTitle(dto.getTitle())
                .setDescription(dto.getDescription())
                .setLocalDateTime(dto.getLocalDateTime())
                .setPlace(dto.getPlace());
        entity.setHost(
                new Host()
                        .setName(dto.getHost().getName())
                        .setEmail(dto.getHost().getEmail())
        );
        entity.setMonetaryAmount(
                new MonetaryAmount()
                        .setNumber(dto.getMonetaryAmount().getNumber())
                        .setCurrency(dto.getMonetaryAmount().getCurrency().getCurrencyCode())
        );
        return entity;
    }
}
