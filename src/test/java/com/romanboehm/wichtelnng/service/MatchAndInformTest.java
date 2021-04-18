package com.romanboehm.wichtelnng.service;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.romanboehm.wichtelnng.model.entity.Event;
import com.romanboehm.wichtelnng.model.entity.Participant;
import com.romanboehm.wichtelnng.repository.EventRepository;
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
public class MatchAndInformTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(SMTP_IMAP)
            .withConfiguration(aConfig().withDisabledAuthentication());

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MatchAndInform matchAndInform;

    @AfterEach
    public void cleanup() {
        eventRepository.deleteAll();
    }

    @Test
    public void shouldMatchAndInform() {
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

        matchAndInform.matchAndInform();

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
    public void shouldDeleteEventsWhoseDeadlineHasPassed() {
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

        matchAndInform.matchAndInform();

        assertThat(greenMail.waitForIncomingEmail(1500, 3)).isTrue();

        assertThat(eventRepository.findAll())
                .contains(open)
                .doesNotContain(deleted);
    }

    @Test
    public void shouldInformHostAboutEmptyEvent() {
        eventRepository.save(event()
                .setDeadline(now().minus(1, DAYS)) // Should be included
                .addParticipant(
                        new Participant()
                                .setName("Angus Young")
                                .setEmail("angusyoung@acdc.net")
                )
        );

        matchAndInform.matchAndInform();

        assertThat(greenMail.waitForIncomingEmail(1500, 1)).isTrue();
        assertThat(greenMail.getReceivedMessages())
                .extracting(mimeMessage -> mimeMessage.getAllRecipients()[0])
                .extracting(Address::toString)
                .contains("georgeyoung@acdc.net")
                .doesNotContain("angusyoung@acdc.net");
    }
}