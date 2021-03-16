package com.romanboehm.wichtelnng.service;


import com.romanboehm.wichtelnng.CustomSpringBootTest;
import com.romanboehm.wichtelnng.model.dto.EventCreation;
import com.romanboehm.wichtelnng.model.dto.ParticipantRegistration;
import com.romanboehm.wichtelnng.model.entity.Event;
import com.romanboehm.wichtelnng.repository.EventRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static com.romanboehm.wichtelnng.TestData.event;
import static com.romanboehm.wichtelnng.TestData.eventCreation;
import static java.time.LocalDateTime.now;
import static java.time.Month.JUNE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@CustomSpringBootTest
public class WichtelnServiceTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private WichtelnService wichtelnService;

    @AfterEach
    public void cleanup() {
        eventRepository.deleteAll();
    }

    @Test
    public void shouldSave() {
        wichtelnService.save(eventCreation());
        assertThat(eventRepository.findAll())
                .hasOnlyOneElementSatisfying(event -> {
                    assertThat(event.getId()).isNotNull();

                    assertThat(event.getTitle()).isEqualTo("AC/DC Secret Santa");
                    assertThat(event.getDescription()).isEqualTo("There's gonna be some santa'ing");
                    assertThat(event.getMonetaryAmount().getNumber()).isEqualByComparingTo("78.50");
                    assertThat(event.getMonetaryAmount().getCurrency()).isEqualTo("AUD");
                    assertThat(event.getHost().getName()).isEqualTo("George Young");
                    assertThat(event.getHost().getEmail()).isEqualTo("georgeyoung@acdc.net");
                    assertThat(event.getMonetaryAmount().getCurrency()).isEqualTo("AUD");
                    assertThat(event.getMonetaryAmount().getCurrency()).isEqualTo("AUD");
                    assertThat(event.getLocalDateTime()).isEqualTo(
                            LocalDateTime.of(
                                    LocalDate.of(2666, JUNE, 7),
                                    LocalTime.of(6, 6)
                            )
                    );
                    assertThat(event.getZoneId()).isEqualTo(ZoneId.of("Australia/Sydney"));
                });
    }

    @Test
    public void shouldNoticeWhenEventPastDeadline() {
        Event pastDeadline = eventRepository.save(event()
                .setLocalDateTime(now().minus(1, ChronoUnit.MINUTES))
                .setZoneId(ZoneId.systemDefault())
        );

        Optional<EventCreation> possibleEvent = wichtelnService.getEvent(pastDeadline.getId());
        assertThat(possibleEvent).isEmpty();
    }

    @Test
    public void shouldPreventParticipantFromRegisteringMultipleTimesForSameEvent() {
        Event eventA = eventRepository.save(event().setTitle("A"));
        Event eventB = eventRepository.save(event().setTitle("B"));

        wichtelnService.register(
                eventA.getId(),
                ParticipantRegistration.with(EventCreation.from(eventA))
                        .setParticipantName("Angus Young")
                        .setParticipantEmail("angusyoung@acdc.net")
        );

        wichtelnService.register(
                eventA.getId(),
                ParticipantRegistration.with(EventCreation.from(eventA))
                        .setParticipantName("Angus Young")
                        .setParticipantEmail("angusyoung@acdc.net")
        );

        wichtelnService.register(
                eventB.getId(),
                ParticipantRegistration.with(EventCreation.from(eventB))
                        .setParticipantName("Angus Young")
                        .setParticipantEmail("angusyoung@acdc.net")
        );

        assertThat(eventRepository.findAll()).extracting(Event::getTitle, event -> event.getParticipants().size())
                .containsExactlyInAnyOrder(
                        tuple("A", 1),
                        tuple("B", 1)
                );

    }

}