package com.romanboehm.wichtelnng.usecases.registerparticipant;

import com.romanboehm.wichtelnng.data.Deadline;
import com.romanboehm.wichtelnng.data.Event;
import com.romanboehm.wichtelnng.data.EventRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.romanboehm.wichtelnng.TestData.event;
import static com.romanboehm.wichtelnng.TestData.zoneId;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@SpringBootTest
class RegisterParticipantServiceTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegisterParticipantService service;

    @MockBean
    private RegisterParticipantNotifier participantNotifier;

    @AfterEach
    public void cleanup() {
        eventRepository.deleteAll();
    }

    @Test
    void shouldNoticeWhenEventPastDeadline() {
        Event pastDeadline = eventRepository.save(event()
                .setDeadline(
                        new Deadline()
                                .setLocalDateTime(LocalDateTime.now().minus(1, MINUTES))
                                .setZoneId(zoneId())
                )
        );

        Optional<Event> possibleEvent = service.getEvent(pastDeadline.getId());
        assertThat(possibleEvent).isEmpty();
    }

    @Test
    void shouldPreventParticipantFromRegisteringMultipleTimesForSameEvent() {
        Event eventA = eventRepository.save(event().setTitle("A"));
        Event eventB = eventRepository.save(event().setTitle("B"));

        service.register(
                eventA.getId(),
                RegisterParticipant.with(eventA)
                        .setParticipantName("Angus Young")
                        .setParticipantEmail("angusyoung@acdc.net")
        );

        service.register(
                eventA.getId(),
                RegisterParticipant.with(eventA)
                        .setParticipantName("Angus Young")
                        .setParticipantEmail("angusyoung@acdc.net")
        );

        service.register(
                eventB.getId(),
                RegisterParticipant.with(eventB)
                        .setParticipantName("Angus Young")
                        .setParticipantEmail("angusyoung@acdc.net")
        );

        assertThat(eventRepository.findAll())
                .extracting(Event::getTitle, event -> event.getParticipants().size())
                .containsExactlyInAnyOrder(
                        tuple("A", 1),
                        tuple("B", 1)
                );

    }

}