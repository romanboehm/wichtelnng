package com.romanboehm.wichtelnng.model.dto;

import javax.money.Monetary;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class EventCreation {
    @NotNull
    @Valid
    private EventDto event;

    public static EventCreation withMinimalDefaults() {
            EventDto event = new EventDto();
            MonetaryAmountDto monetaryAmount = new MonetaryAmountDto();
            monetaryAmount.setCurrency(Monetary.getCurrency("EUR")); // set default currency
            event.setMonetaryAmount(monetaryAmount);
            event.setHost(new HostDto());
            return new EventCreation().setEvent(event);
    }

    public EventDto getEvent() {
        return event;
    }

    public EventCreation setEvent(EventDto event) {
        this.event = event;
        return this;
    }

    @Override
    public String toString() {
        return String.format("EventCreation(event=%s)", event != null ? event : "");
    }
}
