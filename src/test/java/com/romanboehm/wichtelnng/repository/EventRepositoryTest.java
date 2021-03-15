package com.romanboehm.wichtelnng.repository;

import com.romanboehm.wichtelnng.model.entity.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static com.romanboehm.wichtelnng.TestData.event;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void shouldHandleZonedDatetimeQueriesCorrectly() {
        Event sameZoneIncluded = eventRepository.save(event().setZonedDateTime(
                ZonedDateTime.of(
                        LocalDate.of(2021, Month.MARCH, 13),
                        LocalTime.of(10, 0),
                        ZoneId.of("Europe/Berlin")
                )
        ));
        Event sameZoneExcluded = eventRepository.save(event().setZonedDateTime(
                ZonedDateTime.of(
                        LocalDate.of(2021, Month.MARCH, 13),
                        LocalTime.of(11, 1),
                        ZoneId.of("Europe/Berlin")
                )
        ));
        Event differentZoneIncluded = eventRepository.save(event().setZonedDateTime(
                ZonedDateTime.of(
                        LocalDate.of(2021, Month.MARCH, 13),
                        LocalTime.of(9, 0),
                        ZoneId.of("UTC")
                )
        ));
        Event differentZoneExcluded = eventRepository.save(event().setZonedDateTime(
                ZonedDateTime.of(
                        LocalDate.of(2021, Month.MARCH, 13),
                        LocalTime.of(10, 1),
                        ZoneId.of("UTC")
                )
        ));

        ZonedDateTime queryTime = ZonedDateTime.of(
                LocalDate.of(2021, Month.MARCH, 13),
                LocalTime.of(11, 0),
                ZoneId.of("Europe/Berlin")
        );
        List<Event> events = eventRepository.findAllByZonedDateTimeBefore(queryTime);

        assertThat(events).extracting(Event::getId)
                .containsExactlyInAnyOrder(sameZoneIncluded.getId(), differentZoneIncluded.getId());
    }
}