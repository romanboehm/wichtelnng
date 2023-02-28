package com.romanboehm.wichtelnng.usecases.notify;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.romanboehm.wichtelnng.MailUtils;
import com.romanboehm.wichtelnng.data.Deadline;
import com.romanboehm.wichtelnng.data.Host;
import com.romanboehm.wichtelnng.data.Participant;
import com.romanboehm.wichtelnng.data.TestEventRepository;
import jakarta.mail.internet.MimeMessage;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.util.concurrent.TimeUnit;

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
            .withConfiguration(aConfig().withDisabledAuthentication());

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
                                .setEmail("angusyoung@matched.acdc.net"))
                .addParticipant(
                        new Participant()
                                .setName("Malcolm Young")
                                .setEmail("malcolmyoung@matched.acdc.net"))
                .addParticipant(
                        new Participant()
                                .setName("Phil Rudd")
                                .setEmail("philrudd@matched.acdc.net")));

        service.notify(event.getId());
        Awaitility.await().atMost(1500, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            var mails = MailUtils.findMailFor(greenMail, "angusyoung@matched.acdc.net", "malcolmyoung@matched.acdc.net", "philrudd@matched.acdc.net");
            assertThat(mails)
                    .hasSize(3)
                    .extracting(MimeMessage::getContent)
                    .extracting(Object::toString)
                    .allSatisfy(s -> assertThat(s).contains("We matched the event's participants and you're therefore now asked to give a gift to"));
        });
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
                                .setEmail("angusyoung@acdc.net"))
                .addParticipant(
                        new Participant()
                                .setName("Malcolm Young")
                                .setEmail("malcolmyoung@acdc.net"))
                .addParticipant(
                        new Participant()
                                .setName("George Young")
                                .setEmail("georgeyoung@acdc.net")));

        service.notify(deleted.getId());

        assertThat(eventRepository.findById(deleted.getId())).isEmpty();
    }

    @Test
    void shouldInformHostAboutLostEvent() {
        var lostevent = eventRepository.saveAndFlush(event()
                .setHost(new Host()
                        .setName("George Young")
                        .setEmail("georgeyoung@lostevent.acdc.net"))
                .addParticipant(
                        new Participant()
                                .setName("Angus Young")
                                .setEmail("angusyoung@lostevent.acdc.net")));

        service.notify(lostevent.getId());

        Awaitility.await().atMost(1500, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            var possibleHostMail = MailUtils.findMailFor(greenMail, "georgeyoung@lostevent.acdc.net");
            assertThat(possibleHostMail).isNotEmpty();
            assertThat(possibleHostMail.get().getContent()).asInstanceOf(InstanceOfAssertFactories.STRING).contains(
                    "Unfortunately nobody has registered to wichtel");

            var possibleParticipantMail = MailUtils.findMailFor(greenMail, "angusyoung@lostevent.acdc.net");
            assertThat(possibleParticipantMail).isEmpty();
        });
    }
}