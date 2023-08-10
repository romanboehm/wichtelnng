package com.romanboehm.wichtelnng.usecases.createevent;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CreateEventTest {

    @Test
    void shouldSortTimezonesByOffset() {
        List<CreateEvent.EventZoneId> timezones = new CreateEvent().getTimezones();

        LocalDateTime now = LocalDateTime.now();
        assertThat(timezones).isSortedAccordingTo((t1, t2) -> t2.zoneId().getRules().getOffset(now).compareTo(t1.zoneId().getRules().getOffset(now)));
    }

    @Test
    void shouldSortCurrenciesAlphabetically() {
        List<Currency> currencies = new CreateEvent().getCurrencies();

        assertThat(currencies).isSortedAccordingTo(Comparator.comparing(Currency::getCurrencyCode));
    }
}