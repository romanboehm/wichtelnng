package com.romanboehm.wichtelnng.usecases.matchandnotify;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.romanboehm.wichtelnng.data.Deadline;
import com.romanboehm.wichtelnng.data.Event;
import com.romanboehm.wichtelnng.data.EventRepository;
import com.romanboehm.wichtelnng.data.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.Address;

import static com.icegreen.greenmail.configuration.GreenMailConfiguration.aConfig;
import static com.icegreen.greenmail.util.ServerSetupTest.SMTP_IMAP;
import static com.romanboehm.wichtelnng.TestData.event;
import static com.romanboehm.wichtelnng.TestData.zoneId;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class MatchAndNotifyServiceTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(SMTP_IMAP)
            .withConfiguration(aConfig().withDisabledAuthentication());

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MatchAndNotifyService service;

    @BeforeEach
    public void cleanup() {
        eventRepository.deleteAll();
    }

    @Test
    void shouldMatchAndNotify() {
        eventRepository.save(event()
                .setDeadline(
                        new Deadline()
                                .setLocalDateTime(now().minus(1, MINUTES))
                                .setZoneId(zoneId())

                ) // Should be included
                .addParticipant(
                        new Participant()
                                .setName("Angus Young")
                                .setEmail("angusyoung@acdc.net")
                ).addParticipant(
                        new Participant()
                                .setName("Malcolm Young")
                                .setEmail("malcolmyoung@acdc.net")
                ).addParticipant(
                        new Participant()
                                .setName("George Young")
                                .setEmail("georgeyoung@acdc.net")
                )
        );

        eventRepository.save(event()
                .setDeadline(
                        new Deadline()
                                .setLocalDateTime(now().plus(1, MINUTES))
                                .setZoneId(zoneId())

                ) // Should not be included
                .addParticipant(
                        new Participant()
                                .setName("Phil Rudd")
                                .setEmail("philrudd@acdc.net")
                ).addParticipant(
                        new Participant()
                                .setName("Bon Scott")
                                .setEmail("bonscott@acdc.net")
                ).addParticipant(
                        new Participant()
                                .setName("Cliff Williams")
                                .setEmail("cliffwilliams@acdc.net")
                )
        );

        service.matchAndNotify();

        assertThat(greenMail.waitForIncomingEmail(1500, 3)).isTrue();
        assertThat(greenMail.getReceivedMessages())
                .extracting(mimeMessage -> mimeMessage.getAllRecipients()[0])
                .extracting(Address::toString)
                .containsExactlyInAnyOrder(
                        "angusyoung@acdc.net",
                        "malcolmyoung@acdc.net",
                        "georgeyoung@acdc.net"
                );
    }

    @Test
    void shouldDeleteEventsWhoseDeadlineHasPassed() {
        Event deleted = eventRepository.save(event()
                .setDeadline(
                        new Deadline()
                                .setLocalDateTime(now().minus(1, MINUTES))
                                .setZoneId(zoneId())

                ) // Should be included
                .addParticipant(
                        new Participant()
                                .setName("Angus Young")
                                .setEmail("angusyoung@acdc.net")
                ).addParticipant(
                        new Participant()
                                .setName("Malcolm Young")
                                .setEmail("malcolmyoung@acdc.net")
                ).addParticipant(
                        new Participant()
                                .setName("George Young")
                                .setEmail("georgeyoung@acdc.net")
                )
        );

        Event open = eventRepository.save(event()
                .setDeadline(
                        new Deadline()
                                .setLocalDateTime(now().plus(2, MINUTES))
                                .setZoneId(zoneId())

                ) // Should not be included
                .addParticipant(
                        new Participant()
                                .setName("Phil Rudd")
                                .setEmail("philrudd@acdc.net")
                ).addParticipant(
                        new Participant()
                                .setName("Bon Scott")
                                .setEmail("bonscott@acdc.net")
                ).addParticipant(
                        new Participant()
                                .setName("Cliff Williams")
                                .setEmail("cliffwilliams@acdc.net")
                )
        );

        service.matchAndNotify();

        assertThat(greenMail.waitForIncomingEmail(1500, 3)).isTrue();

        assertThat(eventRepository.findAll())
                .contains(open)
                .doesNotContain(deleted);
    }

    @Test
    void shouldInformHostAboutEmptyEvent() {
        eventRepository.save(event()
                .setDeadline(
                        new Deadline()
                                .setLocalDateTime(now().minus(1, MINUTES))
                                .setZoneId(zoneId())

                ) // Should be included
                .addParticipant(
                        new Participant()
                                .setName("Angus Young")
                                .setEmail("angusyoung@acdc.net")
                )
        );

        service.matchAndNotify();

        assertThat(greenMail.waitForIncomingEmail(1500, 1)).isTrue();
        assertThat(greenMail.getReceivedMessages())
                .extracting(mimeMessage -> mimeMessage.getAllRecipients()[0])
                .extracting(Address::toString)
                .contains("georgeyoung@acdc.net")
                .doesNotContain("angusyoung@acdc.net");
    }
}