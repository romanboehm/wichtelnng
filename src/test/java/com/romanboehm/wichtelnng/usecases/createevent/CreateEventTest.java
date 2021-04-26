package com.romanboehm.wichtelnng.usecases.createevent;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CreateEventTest {

    @Test
    void shouldSortTimezonesByOffset() {
        List<CreateEvent.EventZoneId> timezones = new CreateEvent().getTimezones();

        LocalDateTime now = LocalDateTime.now();
        assertThat(timezones).isSortedAccordingTo((t1, t2) ->
            t2.getZoneId().getRules().getOffset(now).compareTo(t1.getZoneId().getRules().getOffset(now))
        );
    }
}