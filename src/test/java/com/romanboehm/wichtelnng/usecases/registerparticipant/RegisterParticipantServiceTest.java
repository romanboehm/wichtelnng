package com.romanboehm.wichtelnng.usecases.registerparticipant;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.store.FolderException;
import com.romanboehm.wichtelnng.common.data.Deadline;
import com.romanboehm.wichtelnng.utils.MailUtils;
import com.romanboehm.wichtelnng.utils.TestEventRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

import static com.icegreen.greenmail.configuration.GreenMailConfiguration.aConfig;
import static com.icegreen.greenmail.util.ServerSetupTest.SMTP_IMAP;
import static com.romanboehm.wichtelnng.utils.GlobalTestData.event;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(webEnvironment = NONE)
class RegisterParticipantServiceTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(SMTP_IMAP)
            .withConfiguration(aConfig().withDisabledAuthentication());

    @Autowired
    private TestEventRepository eventRepository;

    @Autowired
    private RegisterParticipantService service;

    @BeforeEach
    public void cleanup() throws FolderException {
        eventRepository.deleteAllInBatch();
        eventRepository.flush();
        greenMail.purgeEmailFromAllMailboxes();
    }

    @Test
    void handlesEventPastDeadline() {
        var nowMinusOneMinute = ZonedDateTime.now(ZoneId.systemDefault()).minusMinutes(1);
        var pastDeadline = eventRepository.saveAndFlush(event()
                .setDeadline(
                        new Deadline()
                                .setLocalDateTime(nowMinusOneMinute.toLocalDateTime())
                                .setZoneId(nowMinusOneMinute.getZone().getId())));

        assertThatThrownBy(() -> service.getEvent(pastDeadline.getId())).isInstanceOf(RegistrationAttemptTooLateException.class);
    }

    @Test
    void preventsParticipantFromRegisteringMultipleTimes() throws DuplicateParticipantException {
        var event = eventRepository.saveAndFlush(event());

        service.register(
                event.getId(),
                RegisterParticipant.registerFor(event)
                        .setParticipantName("Angus Young")
                        .setParticipantEmail("angusyoung@acdc.net"));

        assertThatThrownBy(() -> service.register(
                event.getId(),
                RegisterParticipant.registerFor(event)
                        .setParticipantName("Angus Young")
                        .setParticipantEmail("angusyoung@acdc.net")))
                .isInstanceOf(DuplicateParticipantException.class);

        assertThat(eventRepository.findByIdWithParticipants(event.getId())).hasValueSatisfying(e -> assertThat(e.getParticipants()).hasSize(1));
    }

    @Test
    void sendsRegistrationMailToParticipant() throws DuplicateParticipantException {
        var event = eventRepository.saveAndFlush(event());

        service.register(
                event.getId(),
                RegisterParticipant.registerFor(event)
                        .setParticipantName("Angus Young")
                        .setParticipantEmail("angusyoung@participantregistration.acdc.net"));

        Awaitility.await().atMost(1500, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            var registrationMail = MailUtils.findMailFor(greenMail, "angusyoung@participantregistration.acdc.net");
            assertThat(registrationMail)
                    .singleElement()
                    .satisfies(mimeMessage -> assertThat(mimeMessage.getContent().toString()).contains("You have successfully registered to wichtel"));
        });
    }

}