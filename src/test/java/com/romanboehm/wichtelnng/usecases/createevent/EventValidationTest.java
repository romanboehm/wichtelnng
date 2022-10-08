package com.romanboehm.wichtelnng.usecases.createevent;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static com.romanboehm.wichtelnng.usecases.createevent.CreateEventTestData.createEvent;
import static java.time.temporal.ChronoUnit.HOURS;
import static javax.validation.Validation.buildDefaultValidatorFactory;
import static org.assertj.core.api.Assertions.assertThat;

class EventValidationTest {

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
    class EventMonetaryAmount {

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
    class EventTitle {

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
    class EventDescription {

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
    class EventZonedDateTime {

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
    class EventHost {

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

}