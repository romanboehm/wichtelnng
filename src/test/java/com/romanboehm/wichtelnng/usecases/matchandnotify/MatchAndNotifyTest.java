package com.romanboehm.wichtelnng.usecases.matchandnotify;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.romanboehm.wichtelnng.data.Event;
import com.romanboehm.wichtelnng.data.Participant;
import com.romanboehm.wichtelnng.data.EventRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.Address;

import static com.icegreen.greenmail.configuration.GreenMailConfiguration.aConfig;
import static com.icegreen.greenmail.util.ServerSetupTest.SMTP_IMAP;
import static com.romanboehm.wichtelnng.TestData.event;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MatchAndNotifyTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(SMTP_IMAP)
            .withConfiguration(aConfig().withDisabledAuthentication());

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MatchAndNotifyService service;

    @AfterEach
    public void cleanup() {
        eventRepository.deleteAll();
    }

    @Test
    void shouldMatchAndInform() {
        eventRepository.save(event()
                .setDeadline(now().minus(1, DAYS)) // Should be included
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
                .setDeadline(now().plus(1, DAYS)) // Should be included
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

        service.matchAndInform();

        assertThat(greenMail.waitForIncomingEmail(1500, 3)).isTrue();
        assertThat(greenMail.getReceivedMessages())
                .extracting(mimeMessage -> mimeMessage.getAllRecipients()[0])
                .extracting(Address::toString)
                .contains(
                        "angusyoung@acdc.net",
                        "malcolmyoung@acdc.net",
                        "georgeyoung@acdc.net"
                );
    }

    @Test
    void shouldDeleteEventsWhoseDeadlineHasPassed() {
        Event deleted = eventRepository.save(event()
                .setDeadline(now().minus(1, DAYS)) // Should be included
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
                .setDeadline(now().plus(1, DAYS)) // Should be included
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

        service.matchAndInform();

        assertThat(greenMail.waitForIncomingEmail(1500, 3)).isTrue();

        assertThat(eventRepository.findAll())
                .contains(open)
                .doesNotContain(deleted);
    }

    @Test
    void shouldInformHostAboutEmptyEvent() {
        eventRepository.save(event()
                .setDeadline(now().minus(1, DAYS)) // Should be included
                .addParticipant(
                        new Participant()
                                .setName("Angus Young")
                                .setEmail("angusyoung@acdc.net")
                )
        );

        service.matchAndInform();

        assertThat(greenMail.waitForIncomingEmail(1500, 1)).isTrue();
        assertThat(greenMail.getReceivedMessages())
                .extracting(mimeMessage -> mimeMessage.getAllRecipients()[0])
                .extracting(Address::toString)
                .contains("georgeyoung@acdc.net")
                .doesNotContain("angusyoung@acdc.net");
    }
}