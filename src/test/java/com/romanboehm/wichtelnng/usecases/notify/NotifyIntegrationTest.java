package com.romanboehm.wichtelnng.usecases.notify;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.romanboehm.wichtelnng.common.data.Deadline;
import com.romanboehm.wichtelnng.common.data.Host;
import com.romanboehm.wichtelnng.common.data.Participant;
import com.romanboehm.wichtelnng.utils.MailUtils;
import com.romanboehm.wichtelnng.utils.TestEventRepository;
import jakarta.mail.internet.MimeMessage;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static com.icegreen.greenmail.configuration.GreenMailConfiguration.aConfig;
import static com.icegreen.greenmail.util.ServerSetupTest.SMTP_IMAP;
import static com.romanboehm.wichtelnng.utils.GlobalTestData.event;
import static java.time.LocalDateTime.now;
import static java.time.ZoneId.systemDefault;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(webEnvironment = NONE)
class NotifyIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(SMTP_IMAP)
            .withConfiguration(aConfig().withDisabledAuthentication());

    @Autowired
    private TestEventRepository eventRepository;

    @Autowired
    private NotifyService service;

    @BeforeEach
    public void cleanup() {
        eventRepository.deleteAll();
    }

    @Test
    void matchesNotifiesDeletes() {
        var event = eventRepository.saveAndFlush(event()
                .setDeadline(
                        new Deadline()
                                .setLocalDateTime(now().minusMinutes(1))
                                .setZoneId(systemDefault())

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

        assertThat(eventRepository.findById(event.getId())).isEmpty();
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
    void informsHostAboutLostEventThenDeletes() {
        var event = eventRepository.saveAndFlush(event()
                .setHost(new Host()
                        .setName("George Young")
                        .setEmail("georgeyoung@lostevent.acdc.net"))
                .addParticipant(
                        new Participant()
                                .setName("Angus Young")
                                .setEmail("angusyoung@lostevent.acdc.net")));

        service.notify(event.getId());

        assertThat(eventRepository.findById(event.getId())).isEmpty();
        Awaitility.await().atMost(1500, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            var hostMail = MailUtils.findMailFor(greenMail, "georgeyoung@lostevent.acdc.net");
            assertThat(hostMail)
                    .singleElement()
                    .satisfies(mimeMessage -> assertThat(mimeMessage.getContent().toString()).contains("Unfortunately nobody has registered to wichtel"));

            var participantMail = MailUtils.findMailFor(greenMail, "angusyoung@lostevent.acdc.net");
            assertThat(participantMail).isEmpty();
        });
    }
}