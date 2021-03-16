package com.romanboehm.wichtelnng.service;


import com.romanboehm.wichtelnng.CustomSpringBootTest;
import com.romanboehm.wichtelnng.model.dto.EventCreation;
import com.romanboehm.wichtelnng.model.dto.ParticipantRegistration;
import com.romanboehm.wichtelnng.model.entity.Event;
import com.romanboehm.wichtelnng.model.entity.Participant;
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
    public void shouldPreventParticipantFromRegisteringMultipleTimes() {
        Event event = eventRepository.save(event()
                .addParticipant(
                        new Participant()
                                .setName("Angus Young")
                                .setEmail("angusyoung@acdc.net")
                )
        );

        wichtelnService.register(
                event.getId(),
                ParticipantRegistration.with(EventCreation.from(event))
                        .setParticipantName("Angus Young")
                        .setParticipantEmail("angusyoung@acdc.net")
        );

        wichtelnService.register(
                event.getId(),
                ParticipantRegistration.with(EventCreation.from(event))
                        .setParticipantName("Angus Young")
                        .setParticipantEmail("angusyoung@acdc.net")
        );

        assertThat(event.getParticipants())
                .hasOnlyOneElementSatisfying(participant -> {
                    assertThat(participant.getName()).isEqualTo("Angus Young");
                    assertThat(participant.getEmail()).isEqualTo("angusyoung@acdc.net");
                });

    }

}