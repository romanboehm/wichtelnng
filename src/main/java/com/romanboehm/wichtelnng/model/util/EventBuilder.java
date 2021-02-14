package com.romanboehm.wichtelnng.model.util;

import com.romanboehm.wichtelnng.model.dto.EventDto;
import com.romanboehm.wichtelnng.model.dto.HostDto;
import com.romanboehm.wichtelnng.model.dto.MonetaryAmountDto;
import com.romanboehm.wichtelnng.model.entity.Event;
import com.romanboehm.wichtelnng.model.entity.Host;
import com.romanboehm.wichtelnng.model.entity.MonetaryAmount;

import javax.money.Monetary;
import java.util.UUID;

public class EventBuilder {

    public static Event fromDto(EventDto dto) {
        return new Event()
                .setId(UUID.randomUUID())
                .setTitle(dto.getTitle())
                .setDescription(dto.getDescription())
                .setLocalDateTime(dto.getLocalDateTime())
                .setHost(
                        new Host()
                                .setName(dto.getHost().getName())
                                .setEmail(dto.getHost().getEmail())
                )
                .setMonetaryAmount(
                        new MonetaryAmount()
                                .setNumber(dto.getMonetaryAmount().getNumber())
                                .setCurrency(dto.getMonetaryAmount().getCurrency().getCurrencyCode())
                );
    }

    public static EventDto fromEntity(Event entity) {
        return new EventDto()
                .setId(entity.getId())
                .setTitle(entity.getTitle())
                .setDescription(entity.getDescription())
                .setMonetaryAmount(
                        new MonetaryAmountDto()
                                .setCurrency(Monetary.getCurrency(entity.getMonetaryAmount().getCurrency()))
                                .setNumber(entity.getMonetaryAmount().getNumber())
                )
                .setLocalDate(entity.getLocalDateTime().toLocalDate())
                .setLocalTime(entity.getLocalDateTime().toLocalTime())
                .setHost(
                        new HostDto()
                                .setName(entity.getHost().getName())
                                .setEmail(entity.getHost().getEmail())
                );
    }
}
