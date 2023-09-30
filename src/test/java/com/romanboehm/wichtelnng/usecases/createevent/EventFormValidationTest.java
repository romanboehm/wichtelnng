package com.romanboehm.wichtelnng.usecases.createevent;

import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static com.romanboehm.wichtelnng.usecases.createevent.CreateEventTestData.eventForm;
import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;

class EventFormValidationTest {

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
        var form = eventForm();

        assertThat(validator.validate(form)).isEmpty();
    }

    @Nested
    class EventMonetaryAmount {

        @Test
        void shouldFailEventWithInvalidNumber() {
            var form = eventForm()
                    .setNumber(BigDecimal.valueOf(-1));

            assertThat(validator.validate(form)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithNullMonetaryAmount() {
            var form = eventForm()
                    .setNumber(null);

            assertThat(validator.validate(form)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithNullCurrency() {
            var form = eventForm()
                    .setCurrency(null);

            assertThat(validator.validate(form)).isNotEmpty();
        }

    }

    @Nested
    class EventTitle {

        @Test
        void shouldFailEventWithTooLongTitle() {
            var form = eventForm()
                    .setTitle("foo".repeat(100));

            assertThat(validator.validate(form)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithNullTitle() {
            var form = eventForm()
                    .setTitle(null);

            assertThat(validator.validate(form)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithEmptyTitle() {
            var form = eventForm()
                    .setTitle("");

            assertThat(validator.validate(form)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithWhitespaceTitle() {
            var form = eventForm()
                    .setTitle(" ");

            assertThat(validator.validate(form)).isNotEmpty();
        }
    }

    @Nested
    class EventDescription {

        @Test
        void shouldFailEventWithTooLongDescription() {
            var form = eventForm()
                    .setDescription("foo".repeat(1000));

            assertThat(validator.validate(form)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithNullDescription() {
            var form = eventForm()
                    .setDescription(null);

            assertThat(validator.validate(form)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithEmptyDescription() {
            var form = eventForm()
                    .setDescription("");

            assertThat(validator.validate(form)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithWhitespaceDescription() {
            var form = eventForm()
                    .setDescription(" ");

            assertThat(validator.validate(form)).isNotEmpty();
        }

    }

    @Nested
    class EventZonedDateTime {

        @Test
        void shouldFailEventWithPastZonedDateTime() {
            var form = eventForm()
                    .setLocalDate(LocalDate.now())
                    .setLocalTime(LocalTime.now().minus(1, HOURS));

            assertThat(validator.validate(form)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithNullLocalDate() {
            var form = eventForm()
                    .setLocalDate(null);

            assertThat(validator.validate(form)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithNullLocalTime() {
            var form = eventForm()
                    .setLocalTime(null);

            assertThat(validator.validate(form)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithNullTimezone() {
            var form = eventForm()
                    .setTimezone(null);

            assertThat(validator.validate(form)).isNotEmpty();
        }
    }

    @Nested
    class EventHost {

        @Test
        void shouldFailEventWithTooLongHostName() {
            var form = eventForm()
                    .setHostName("foo".repeat(100));

            assertThat(validator.validate(form)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithNullHostName() {
            var form = eventForm()
                    .setHostName(null);

            assertThat(validator.validate(form)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithEmptyHostName() {
            var form = eventForm()
                    .setHostName("");

            assertThat(validator.validate(form)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithInvalidHostEmail() {
            var form = eventForm()
                    .setHostEmail("notavalid.email");

            assertThat(validator.validate(form)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithNullHostEmail() {
            var form = eventForm()
                    .setHostEmail(null);

            assertThat(validator.validate(form)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithEmptyHostEmail() {
            var form = eventForm()
                    .setHostEmail("");

            assertThat(validator.validate(form)).isNotEmpty();
        }

        @Test
        void shouldFailEventWithWhitespaceHostEmail() {
            var form = eventForm()
                    .setHostEmail(" ");

            assertThat(validator.validate(form)).isNotEmpty();
        }
    }

}