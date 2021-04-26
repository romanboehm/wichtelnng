package com.romanboehm.wichtelnng.validation;

import com.romanboehm.wichtelnng.usecases.createevent.CreateEvent;
import com.romanboehm.wichtelnng.usecases.registerparticipant.RegisterParticipant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static com.romanboehm.wichtelnng.TestData.createEvent;
import static com.romanboehm.wichtelnng.TestData.participantRegistration;
import static java.time.temporal.ChronoUnit.HOURS;
import static javax.validation.Validation.buildDefaultValidatorFactory;
import static org.assertj.core.api.Assertions.assertThat;

public class EventValidationTest {

    private Validator validator;
    private ValidatorFactory validatorFactory;

    @BeforeEach
    public void createValidator() {
        validatorFactory = buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterEach
    public void close() {
        validatorFactory.close();
    }

    @Test
    void shouldAcceptValidEvent() {
        CreateEvent event = createEvent();

        assertThat(validator.validate(event)).isEmpty();
    }

    @Nested
    public class EventMonetaryAmount {

        @Test
        void shouldFailEventWithInvalidNumber() {
            CreateEvent event = createEvent()
                    .setNumber(BigDecimal.valueOf(-1));

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithNullMonetaryAmount() {
            CreateEvent event = createEvent()
                    .setNumber(null);

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithNullCurrency() {
            CreateEvent event = createEvent()
                    .setCurrency(null);

            assertThat(validator.validate(event)).isNotEmpty();
        }

    }

    @Nested
    public class EventTitle {

        @Test
        void shouldFailEventWithTooLongTitle() {
            CreateEvent event = createEvent()
                    .setTitle("foo".repeat(100));

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithNullTitle() {
            CreateEvent event = createEvent()
                    .setTitle(null);

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithEmptyTitle() {
            CreateEvent event = createEvent()
                    .setTitle("");

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithWhitespaceTitle() {
            CreateEvent event = createEvent()
                    .setTitle(" ");

            assertThat(validator.validate(event)).isNotEmpty();
        }
    }

    @Nested
    public class EventDescription {

        @Test
        void shouldFailEventWithTooLongDescription() {
            CreateEvent event = createEvent()
                    .setDescription("foo".repeat(1000));

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithNullDescription() {
            CreateEvent event = createEvent()
                    .setDescription(null);

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithEmptyDescription() {
            CreateEvent event = createEvent()
                    .setDescription("");

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithWhitespaceDescription() {
            CreateEvent event = createEvent()
                    .setDescription(" ");

            assertThat(validator.validate(event)).isNotEmpty();
        }

    }

    @Nested
    public class EventZonedDateTime {

        @Test
        void shouldFailEventWithPastZonedDateTime() {
            CreateEvent event = createEvent()
                    .setLocalDate(LocalDate.now())
                    .setLocalTime(LocalTime.now().minus(1, HOURS));

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithNullLocalDate() {
            CreateEvent event = createEvent()
                    .setLocalDate(null);

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithNullLocalTime() {
            CreateEvent event = createEvent()
                    .setLocalTime(null);

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithNullTimezone() {
            CreateEvent event = createEvent()
                    .setTimezone(null);

            assertThat(validator.validate(event)).isNotEmpty();
        }
    }

    @Nested
    public class EventHost {

        @Test
        void shouldFailEventWithTooLongHostName() {
            CreateEvent event = createEvent()
                    .setHostName("foo".repeat(100));

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithNullHostName() {
            CreateEvent event = createEvent()
                    .setHostName(null);

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithEmptyHostName() {
            CreateEvent event = createEvent()
                    .setHostName("");

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithInvalidHostEmail() {
            CreateEvent event = createEvent()
                    .setHostEmail("notavalid.email");

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithNullHostEmail() {
            CreateEvent event = createEvent()
                    .setHostEmail(null);

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithEmptyHostEmail() {
            CreateEvent event = createEvent()
                    .setHostEmail("");

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithWhitespaceHostEmail() {
            CreateEvent event = createEvent()
                    .setHostEmail(" ");

            assertThat(validator.validate(event)).isNotEmpty();
        }
    }

    @Nested
    public class EventParticipant {

        @Test
        void shouldFailParticipantWithTooLongName() {
            RegisterParticipant registration = participantRegistration()
                    .setParticipantName("foo".repeat(100));

            assertThat(validator.validate(registration)).isNotEmpty();
        }

        @Test
        void shouldFailParticipantWithEmptyName() {
            RegisterParticipant registration = participantRegistration()
                    .setParticipantName("");

            assertThat(validator.validate(registration)).isNotEmpty();
        }

        @Test
        void shouldFailParticipantWithWhitespaceName() {
            RegisterParticipant registration = participantRegistration()
                    .setParticipantName(" ");

            assertThat(validator.validate(registration)).isNotEmpty();
        }

        @Test
        void shouldFailParticipantWithNullName() {
            RegisterParticipant registration = participantRegistration()
                    .setParticipantName(null);

            assertThat(validator.validate(registration)).isNotEmpty();
        }

        @Test
        void shouldFailParticipantWithInvalidEmail() {
            RegisterParticipant registration = participantRegistration()
                    .setParticipantEmail("notavalid.email");

            assertThat(validator.validate(registration)).isNotEmpty();
        }

        @Test
        void shouldFailParticipantWithEmptyEmail() {
            RegisterParticipant registration = participantRegistration()
                    .setParticipantEmail("");

            assertThat(validator.validate(registration)).isNotEmpty();
        }

        @Test
        void shouldFailParticipantWithWhitespaceEmail() {
            RegisterParticipant registration = participantRegistration()
                    .setParticipantEmail(" ");

            assertThat(validator.validate(registration)).isNotEmpty();
        }

        @Test
        void shouldFailParticipantWithNullEmail() {
            RegisterParticipant registration = participantRegistration()
                    .setParticipantEmail(null);

            assertThat(validator.validate(registration)).isNotEmpty();
        }
    }

}