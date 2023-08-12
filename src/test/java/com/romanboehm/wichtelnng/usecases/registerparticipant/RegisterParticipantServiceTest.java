package com.romanboehm.wichtelnng.usecases.registerparticipant;

import com.romanboehm.wichtelnng.common.data.Deadline;
import com.romanboehm.wichtelnng.utils.TestEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.romanboehm.wichtelnng.usecases.registerparticipant.RegisterParticipantTestData.participantRegistration;
import static com.romanboehm.wichtelnng.utils.GlobalTestData.event;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(webEnvironment = NONE)
class RegisterParticipantServiceTest {

    @Autowired
    private TestEventRepository eventRepository;

    @Autowired
    private RegisterParticipantService service;

    @BeforeEach
    public void cleanup() {
        eventRepository.deleteAll();
    }

    @Test
    void handlesEventPastDeadline() {
        var nowMinusOneMinute = ZonedDateTime.now(ZoneId.systemDefault()).minusMinutes(1);
        var pastDeadline = eventRepository.saveAndFlush(event()
                .setDeadline(
                        new Deadline()
                                .setLocalDateTime(nowMinusOneMinute.toLocalDateTime())
                                .setZoneId(nowMinusOneMinute.getZone())));

        assertThatThrownBy(() -> service.getEventOpenForRegistration(pastDeadline.getId())).isInstanceOf(RegistrationAttemptTooLateException.class);
    }

    @Test
    void preventsDuplicateRegistration() throws DuplicateParticipantException {
        var eventId = eventRepository.saveAndFlush(event()).getId();

        service.register(
                eventId,
                participantRegistration()
                        .setParticipantName("Angus Young")
                        .setParticipantEmail("angusyoung@acdc.net"));

        assertThatThrownBy(() -> service.register(
                eventId,
                participantRegistration()
                        .setParticipantName("Angus Young")
                        .setParticipantEmail("angusyoung@acdc.net")))
                .isInstanceOf(DuplicateParticipantException.class);

        assertThat(eventRepository.findByIdWithParticipants(eventId)).hasValueSatisfying(e -> assertThat(e.getParticipants()).hasSize(1));
    }

}