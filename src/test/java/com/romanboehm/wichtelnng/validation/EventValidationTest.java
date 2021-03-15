package com.romanboehm.wichtelnng.validation;

import com.romanboehm.wichtelnng.TestData;
import com.romanboehm.wichtelnng.model.dto.EventCreation;
import com.romanboehm.wichtelnng.model.dto.ParticipantRegistration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class EventValidationTest {

    private Validator validator;
    private ValidatorFactory validatorFactory;

    @BeforeEach
    public void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterEach
    public void close() {
        validatorFactory.close();
    }

    @Test
    public void shouldAcceptValidEvent() {
        EventCreation event = TestData.eventCreation();

        Assertions.assertThat(validator.validate(event)).isEmpty();
    }

    @Nested
    public class EventMonetaryAmount {

        @Test
        public void shouldFailEventWithInvalidNumber() {
            EventCreation event = TestData.eventCreation()
                    .setNumber(BigDecimal.valueOf(-1));

            Assertions.assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullMonetaryAmount() {
            EventCreation event = TestData.eventCreation()
                    .setNumber(null);

            Assertions.assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullCurrency() {
            EventCreation event = TestData.eventCreation()
                    .setCurrency(null);

            Assertions.assertThat(validator.validate(event)).isNotEmpty();
        }

    }

    @Nested
    public class EventTitle {

        @Test
        public void shouldFailEventWithTooLongTitle() {
            EventCreation event = TestData.eventCreation()
                    .setTitle("foo".repeat(100));

            Assertions.assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullTitle() {
            EventCreation event = TestData.eventCreation()
                    .setTitle(null);

            Assertions.assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithEmptyTitle() {
            EventCreation event = TestData.eventCreation()
                    .setTitle("");

            Assertions.assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithWhitespaceTitle() {
            EventCreation event = TestData.eventCreation()
                    .setTitle(" ");

            Assertions.assertThat(validator.validate(event)).isNotEmpty();
        }
    }

    @Nested
    public class EventDescription {

        @Test
        public void shouldFailEventWithTooLongDescription() {
            EventCreation event = TestData.eventCreation()
                    .setDescription("foo".repeat(1000));

            Assertions.assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullDescription() {
            EventCreation event = TestData.eventCreation()
                    .setDescription(null);

            Assertions.assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithEmptyDescription() {
            EventCreation event = TestData.eventCreation()
                    .setDescription("");

            Assertions.assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithWhitespaceDescription() {
            EventCreation event = TestData.eventCreation()
                    .setDescription(" ");

            Assertions.assertThat(validator.validate(event)).isNotEmpty();
        }

    }

    @Nested
    public class EventZonedDateTime {

        @Test
        public void shouldFailEventWithPastZonedDateTime() {
            EventCreation event = TestData.eventCreation()
                    .setLocalDate(LocalDate.now())
                    .setLocalTime(LocalTime.now().minus(1, ChronoUnit.HOURS));

            Assertions.assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullLocalDate() {
            EventCreation event = TestData.eventCreation()
                    .setLocalDate(null);

            Assertions.assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullLocalTime() {
            EventCreation event = TestData.eventCreation()
                    .setLocalTime(null);

            Assertions.assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullTimezone() {
            EventCreation event = TestData.eventCreation()
                    .setTimezone(null);

            Assertions.assertThat(validator.validate(event)).isNotEmpty();
        }
    }

    @Nested
    public class EventHost {

        @Test
        public void shouldFailEventWithTooLongHostName() {
            EventCreation event = TestData.eventCreation()
                    .setHostName("foo".repeat(100));

            Assertions.assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullHostName() {
            EventCreation event = TestData.eventCreation()
                    .setHostName(null);

            Assertions.assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithEmptyHostName() {
            EventCreation event = TestData.eventCreation()
                    .setHostName("");

            Assertions.assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithInvalidHostEmail() {
            EventCreation event = TestData.eventCreation()
                    .setHostEmail("notavalid.email");

            Assertions.assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullHostEmail() {
            EventCreation event = TestData.eventCreation()
                    .setHostEmail(null);

            Assertions.assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithEmptyHostEmail() {
            EventCreation event = TestData.eventCreation()
                    .setHostEmail("");

            Assertions.assertThat(validator.validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithWhitespaceHostEmail() {
            EventCreation event = TestData.eventCreation()
                    .setHostEmail(" ");

            Assertions.assertThat(validator.validate(event)).isNotEmpty();
        }
    }

    @Nested
    public class EventParticipant {

        @Test
        public void shouldFailParticipantWithTooLongName() {
            ParticipantRegistration registration = TestData.participantRegistration()
                    .setParticipantName("foo".repeat(100));

            Assertions.assertThat(validator.validate(registration)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithEmptyName() {
            ParticipantRegistration registration = TestData.participantRegistration()
                    .setParticipantName("");

            Assertions.assertThat(validator.validate(registration)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithWhitespaceName() {
            ParticipantRegistration registration = TestData.participantRegistration()
                    .setParticipantName(" ");

            Assertions.assertThat(validator.validate(registration)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithNullName() {
            ParticipantRegistration registration = TestData.participantRegistration()
                    .setParticipantName(null);

            Assertions.assertThat(validator.validate(registration)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithInvalidEmail() {
            ParticipantRegistration registration = TestData.participantRegistration()
                    .setParticipantEmail("notavalid.email");

            Assertions.assertThat(validator.validate(registration)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithEmptyEmail() {
            ParticipantRegistration registration = TestData.participantRegistration()
                    .setParticipantEmail("");

            Assertions.assertThat(validator.validate(registration)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithWhitespaceEmail() {
            ParticipantRegistration registration = TestData.participantRegistration()
                    .setParticipantEmail(" ");

            Assertions.assertThat(validator.validate(registration)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithNullEmail() {
            ParticipantRegistration registration = TestData.participantRegistration()
                    .setParticipantEmail(null);

            Assertions.assertThat(validator.validate(registration)).isNotEmpty();
        }
    }

}