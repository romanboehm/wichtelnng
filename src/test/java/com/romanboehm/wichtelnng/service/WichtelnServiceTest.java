package com.romanboehm.wichtelnng.service;


import com.romanboehm.wichtelnng.CustomSpringBootTest;
import com.romanboehm.wichtelnng.TestData;
import com.romanboehm.wichtelnng.model.dto.EventCreation;
import com.romanboehm.wichtelnng.model.entity.Event;
import com.romanboehm.wichtelnng.repository.EventRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@CustomSpringBootTest
public class WichtelnServiceTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private WichtelnService wichtelnService;

    @Test
    public void shouldSave() {
        wichtelnService.save(TestData.eventCreation());
        Assertions.assertThat(eventRepository.findAll())
                .hasOnlyOneElementSatisfying(event -> {
                    Assertions.assertThat(event.getId()).isNotNull();

                    Assertions.assertThat(event.getTitle()).isEqualTo("AC/DC Secret Santa");
                    Assertions.assertThat(event.getDescription()).isEqualTo("There's gonna be some santa'ing");
                    Assertions.assertThat(event.getMonetaryAmount().getNumber()).isEqualByComparingTo("78.50");
                    Assertions.assertThat(event.getMonetaryAmount().getCurrency()).isEqualTo("AUD");
                    Assertions.assertThat(event.getHost().getName()).isEqualTo("George Young");
                    Assertions.assertThat(event.getHost().getEmail()).isEqualTo("georgeyoung@acdc.net");
                    Assertions.assertThat(event.getMonetaryAmount().getCurrency()).isEqualTo("AUD");
                    Assertions.assertThat(event.getZonedDateTime()).isEqualTo(
                            ZonedDateTime.of(
                                    LocalDate.of(2666, Month.JUNE, 7),
                                    LocalTime.of(6, 6),
                                    ZoneId.of("Australia/Sydney")
                            )
                    );
                });
    }

    @Test
    public void shouldNoticeWhenEventPastDeadline() {
        Event pastDeadline = eventRepository.save(
                TestData.event().setZonedDateTime(ZonedDateTime.now().minus(1, ChronoUnit.MINUTES))
        );

        Optional<EventCreation> possibleEvent = wichtelnService.getEvent(pastDeadline.getId());
        Assertions.assertThat(possibleEvent).isEmpty();
    }

}