package com.romanboehm.wichtelnng.usecases.registerparticipant;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.store.FolderException;
import com.romanboehm.wichtelnng.data.Deadline;
import com.romanboehm.wichtelnng.data.TestEventRepository;
import jakarta.mail.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static com.icegreen.greenmail.configuration.GreenMailConfiguration.aConfig;
import static com.icegreen.greenmail.util.ServerSetupTest.SMTP_IMAP;
import static com.romanboehm.wichtelnng.GlobalTestData.event;
import static java.time.ZoneId.systemDefault;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(webEnvironment = NONE)
class RegisterParticipantServiceTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(SMTP_IMAP)
            .withConfiguration(aConfig().withDisabledAuthentication())
            .withPerMethodLifecycle(true);

    @Autowired
    private TestEventRepository eventRepository;

    @Autowired
    private RegisterParticipantService service;

    @BeforeEach
    public void cleanup() throws FolderException {
        eventRepository.deleteAllInBatch();
        eventRepository.flush();
    }

    @Test
    void handlesEventPastDeadline() {
        var pastDeadline = eventRepository.saveAndFlush(event()
                .setDeadline(
                        new Deadline()
                                .setLocalDateTime(LocalDateTime.now().minus(1, MINUTES))
                                .setZoneId(systemDefault().getId())));

        var possibleEvent = service.getEvent(pastDeadline.getId());
        assertThat(possibleEvent).isEmpty();
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
                        .setParticipantEmail("angusyoung@acdc.net"));

        assertThat(greenMail.waitForIncomingEmail(1500, 1)).isTrue();
        assertThat(greenMail.getReceivedMessages())
                .extracting(mimeMessage -> mimeMessage.getAllRecipients()[0])
                .extracting(Address::toString)
                .containsExactly("angusyoung@acdc.net");
    }

}