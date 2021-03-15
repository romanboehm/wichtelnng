package com.romanboehm.wichtelnng.service;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.romanboehm.wichtelnng.CustomSpringBootTest;
import com.romanboehm.wichtelnng.TestData;
import com.romanboehm.wichtelnng.model.entity.Event;
import com.romanboehm.wichtelnng.model.entity.Participant;
import com.romanboehm.wichtelnng.repository.EventRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.Address;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@CustomSpringBootTest
public class MatchAndInformTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP_IMAP)
            .withConfiguration(GreenMailConfiguration.aConfig().withDisabledAuthentication());

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
        eventRepository.save(TestData.event()
                .setZonedDateTime(
                        ZonedDateTime.of(
                                LocalDate.now().minus(1, ChronoUnit.DAYS), // Should be included
                                LocalTime.now(),
                                ZoneId.of("Australia/Sydney")
                        )
                ).addParticipant(
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

        eventRepository.save(TestData.event()
                .setZonedDateTime(
                        ZonedDateTime.of(
                                LocalDate.now().plus(1, ChronoUnit.DAYS), // Should be excluded
                                LocalTime.now(),
                                ZoneId.of("Australia/Sydney")
                        )
                ).addParticipant(
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

        Assertions.assertThat(greenMail.waitForIncomingEmail(1500, 3)).isTrue();
        Assertions.assertThat(greenMail.getReceivedMessages())
                .extracting(mimeMessage -> mimeMessage.getAllRecipients()[0])
                .extracting(Address::toString)
                .contains(
                        "angusyoung@acdc.net",
                        "malcolmyoung@acdc.net",
                        "georgeyoung@acdc.net"
                );
    }

    @Test
    public void shouldDeleteEventsWhereParticipantsHaveBeenInformed() {
        Event deleted = eventRepository.save(TestData.event()
                .setZonedDateTime(
                        ZonedDateTime.of(
                                LocalDate.now().minus(1, ChronoUnit.DAYS), // Should be included
                                LocalTime.now(),
                                ZoneId.of("Australia/Sydney")
                        )
                ).addParticipant(
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

        Event open = eventRepository.save(TestData.event()
                .setZonedDateTime(
                        ZonedDateTime.of(
                                LocalDate.now().plus(1, ChronoUnit.DAYS), // Should be excluded
                                LocalTime.now(),
                                ZoneId.of("Australia/Sydney")
                        )
                ).addParticipant(
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

        Assertions.assertThat(greenMail.waitForIncomingEmail(1500, 3)).isTrue();

        Assertions.assertThat(eventRepository.findAll())
                .contains(open)
                .doesNotContain(deleted);
    }

    @Test
    public void shouldInformHostAboutEmptyEvent() {
        eventRepository.save(TestData.event()
                .setZonedDateTime(
                        ZonedDateTime.of(
                                LocalDate.now().minus(1, ChronoUnit.DAYS), // Should be included
                                LocalTime.now(),
                                ZoneId.of("Australia/Sydney")
                        )
                ).addParticipant(
                        new Participant()
                                .setName("Angus Young")
                                .setEmail("angusyoung@acdc.net")
                )
        );

        matchAndInform.matchAndInform();

        Assertions.assertThat(greenMail.waitForIncomingEmail(1500, 1)).isTrue();
        Assertions.assertThat(greenMail.getReceivedMessages())
                .extracting(mimeMessage -> mimeMessage.getAllRecipients()[0])
                .extracting(Address::toString)
                .contains("georgeyoung@acdc.net")
                .doesNotContain("angusyoung@acdc.net");
    }
}