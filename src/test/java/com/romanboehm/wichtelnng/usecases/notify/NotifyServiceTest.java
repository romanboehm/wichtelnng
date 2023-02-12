package com.romanboehm.wichtelnng.usecases.notify;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.romanboehm.wichtelnng.data.Deadline;
import com.romanboehm.wichtelnng.data.Participant;
import com.romanboehm.wichtelnng.data.TestEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.icegreen.greenmail.configuration.GreenMailConfiguration.aConfig;
import static com.icegreen.greenmail.util.ServerSetupTest.SMTP_IMAP;
import static com.romanboehm.wichtelnng.GlobalTestData.event;
import static java.time.LocalDateTime.now;
import static java.time.ZoneId.systemDefault;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(webEnvironment = NONE)
class NotifyServiceTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(SMTP_IMAP)
            .withConfiguration(aConfig().withDisabledAuthentication())
            .withPerMethodLifecycle(true);

    @Autowired
    private TestEventRepository eventRepository;

    @Autowired
    private NotifyService service;

    @BeforeEach
    public void cleanup() {
        eventRepository.deleteAllInBatch();
        eventRepository.flush();
    }

    @Test
    void shouldMatchAndNotify() {
        var event = eventRepository.saveAndFlush(event()
                .setDeadline(
                        new Deadline()
                                .setLocalDateTime(now().minus(1, MINUTES))
                                .setZoneId(systemDefault().getId())

                )
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

        service.notify(event.getId());

        assertThat(greenMail.waitForIncomingEmail(1500, 3)).isTrue();
        assertThat(greenMail.getReceivedMessages())
                .extracting(mimeMessage -> mimeMessage.getAllRecipients()[0])
                .extracting(addr -> addr.toString())
                .containsExactlyInAnyOrder(
                        "angusyoung@acdc.net",
                        "malcolmyoung@acdc.net",
                        "georgeyoung@acdc.net"
                );
    }

    @Test
    void shouldDeleteEventsAfterNotification() {
        var deleted = eventRepository.saveAndFlush(event()
                .setDeadline(
                        new Deadline()
                                .setLocalDateTime(now().minus(1, MINUTES))
                                .setZoneId(systemDefault().getId())

                )
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

        service.notify(deleted.getId());

        assertThat(eventRepository.findById(deleted.getId())).isEmpty();
    }

    @Test
    void shouldInformHostAboutEmptyEvent() {
        var emptyEvent = eventRepository.saveAndFlush(event()
                .setDeadline(
                        new Deadline()
                                .setLocalDateTime(now().minus(1, MINUTES))
                                .setZoneId(systemDefault().getId())

                )
                .addParticipant(
                        new Participant()
                                .setName("Angus Young")
                                .setEmail("angusyoung@acdc.net")
                )
        );

        service.notify(emptyEvent.getId());

        assertThat(greenMail.waitForIncomingEmail(1500, 1)).isTrue();
        assertThat(greenMail.getReceivedMessages())
                .extracting(mimeMessage -> mimeMessage.getAllRecipients()[0])
                .extracting(addr -> addr.toString())
                .contains("georgeyoung@acdc.net")
                .doesNotContain("angusyoung@acdc.net");
    }
}