package com.romanboehm.wichtelnng.validation;

import com.romanboehm.wichtelnng.model.dto.EventCreation;
import com.romanboehm.wichtelnng.model.dto.ParticipantRegistration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static com.romanboehm.wichtelnng.TestData.eventCreation;
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
    public void shouldAcceptValidEvent() {
        EventCreation event = eventCreation();

        assertThat(validator.validate(event)).isEmpty();
    }

    @Nested
    public class EventMonetaryAmount {

        @Test
        public void shouldFailEventWithInvalidNumber() {
            EventCreation event = eventCreation()
                    .setNumber(BigDecimal.valueOf(-1));

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullMonetaryAmount() {
            EventCreation event = eventCreation()
                    .setNumber(null);

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullCurrency() {
            EventCreation event = eventCreation()
                    .setCurrency(null);

            assertThat(validator.validate(event)).isNotEmpty();
        }

    }

    @Nested
    public class EventTitle {

        @Test
        public void shouldFailEventWithTooLongTitle() {
            EventCreation event = eventCreation()
                    .setTitle("foo".repeat(100));

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullTitle() {
            EventCreation event = eventCreation()
                    .setTitle(null);

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithEmptyTitle() {
            EventCreation event = eventCreation()
                    .setTitle("");

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithWhitespaceTitle() {
            EventCreation event = eventCreation()
                    .setTitle(" ");

            assertThat(validator.validate(event)).isNotEmpty();
        }
    }

    @Nested
    public class EventDescription {

        @Test
        public void shouldFailEventWithTooLongDescription() {
            EventCreation event = eventCreation()
                    .setDescription("foo".repeat(1000));

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullDescription() {
            EventCreation event = eventCreation()
                    .setDescription(null);

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithEmptyDescription() {
            EventCreation event = eventCreation()
                    .setDescription("");

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithWhitespaceDescription() {
            EventCreation event = eventCreation()
                    .setDescription(" ");

            assertThat(validator.validate(event)).isNotEmpty();
        }

    }

    @Nested
    public class EventZonedDateTime {

        @Test
        public void shouldFailEventWithPastZonedDateTime() {
            EventCreation event = eventCreation()
                    .setLocalDate(LocalDate.now())
                    .setLocalTime(LocalTime.now().minus(1, HOURS));

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullLocalDate() {
            EventCreation event = eventCreation()
                    .setLocalDate(null);

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullLocalTime() {
            EventCreation event = eventCreation()
                    .setLocalTime(null);

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullTimezone() {
            EventCreation event = eventCreation()
                    .setTimezone(null);

            assertThat(validator.validate(event)).isNotEmpty();
        }
    }

    @Nested
    public class EventHost {

        @Test
        public void shouldFailEventWithTooLongHostName() {
            EventCreation event = eventCreation()
                    .setHostName("foo".repeat(100));

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullHostName() {
            EventCreation event = eventCreation()
                    .setHostName(null);

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithEmptyHostName() {
            EventCreation event = eventCreation()
                    .setHostName("");

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithInvalidHostEmail() {
            EventCreation event = eventCreation()
                    .setHostEmail("notavalid.email");

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullHostEmail() {
            EventCreation event = eventCreation()
                    .setHostEmail(null);

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithEmptyHostEmail() {
            EventCreation event = eventCreation()
                    .setHostEmail("");

            assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithWhitespaceHostEmail() {
            EventCreation event = eventCreation()
                    .setHostEmail(" ");

            assertThat(validator.validate(event)).isNotEmpty();
        }
    }

    @Nested
    public class EventParticipant {

        @Test
        public void shouldFailParticipantWithTooLongName() {
            ParticipantRegistration registration = participantRegistration()
                    .setParticipantName("foo".repeat(100));

            assertThat(validator.validate(registration)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithEmptyName() {
            ParticipantRegistration registration = participantRegistration()
                    .setParticipantName("");

            assertThat(validator.validate(registration)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithWhitespaceName() {
            ParticipantRegistration registration = participantRegistration()
                    .setParticipantName(" ");

            assertThat(validator.validate(registration)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithNullName() {
            ParticipantRegistration registration = participantRegistration()
                    .setParticipantName(null);

            assertThat(validator.validate(registration)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithInvalidEmail() {
            ParticipantRegistration registration = participantRegistration()
                    .setParticipantEmail("notavalid.email");

            assertThat(validator.validate(registration)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithEmptyEmail() {
            ParticipantRegistration registration = participantRegistration()
                    .setParticipantEmail("");

            assertThat(validator.validate(registration)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithWhitespaceEmail() {
            ParticipantRegistration registration = participantRegistration()
                    .setParticipantEmail(" ");

            assertThat(validator.validate(registration)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithNullEmail() {
            ParticipantRegistration registration = participantRegistration()
                    .setParticipantEmail(null);

            assertThat(validator.validate(registration)).isNotEmpty();
        }
    }

}