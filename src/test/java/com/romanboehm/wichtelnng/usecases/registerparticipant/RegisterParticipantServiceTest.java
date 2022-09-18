package com.romanboehm.wichtelnng.usecases.registerparticipant;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.romanboehm.wichtelnng.data.Deadline;
import com.romanboehm.wichtelnng.data.Event;
import com.romanboehm.wichtelnng.data.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.icegreen.greenmail.configuration.GreenMailConfiguration.aConfig;
import static com.icegreen.greenmail.util.ServerSetupTest.SMTP_IMAP;
import static com.romanboehm.wichtelnng.TestData.event;
import static com.romanboehm.wichtelnng.TestData.zoneId;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class RegisterParticipantServiceTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(SMTP_IMAP)
            .withConfiguration(aConfig().withDisabledAuthentication());

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegisterParticipantService service;

    @BeforeEach
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