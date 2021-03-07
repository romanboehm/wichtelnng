package com.romanboehm.wichtelnng.validation;

import com.romanboehm.wichtelnng.TestData;
import com.romanboehm.wichtelnng.model.dto.EventDto;
import com.romanboehm.wichtelnng.model.dto.HostDto;
import com.romanboehm.wichtelnng.model.dto.MonetaryAmountDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class EventValidationTest extends BaseValidationTest {

    @Test
    public void shouldAcceptValidEvent() {
        EventDto event = TestData.event().dto();

        Assertions.assertThat(getValidator().validate(event)).isEmpty();
    }

    @Nested
    public class EventDependencies {

        @Test
        public void shouldFailEventWithInvalidMonetaryAmount() {
            EventDto event = TestData.event().dto();
            MonetaryAmountDto monetaryAmount = new MonetaryAmountDto();
            event.setMonetaryAmount(monetaryAmount);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullMonetaryAmount() {
            EventDto event = TestData.event().dto();
            event.setMonetaryAmount(null);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullHost() {
            EventDto event = TestData.event().dto();
            event.setHost(null);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithInvalidHost() {
            EventDto event = TestData.event().dto();
            event.setHost(new HostDto());

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

    }

    @Nested
    public class EventTitle {

        @Test
        public void shouldFailEventWithTooLongTitle() {
            EventDto event = TestData.event().dto();
            event.setTitle(event.getTitle().repeat(100));

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullTitle() {
            EventDto event = TestData.event().dto();
            event.setTitle(null);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithEmptyTitle() {
            EventDto event = TestData.event().dto();
            event.setTitle("");

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithWhitespaceTitle() {
            EventDto event = TestData.event().dto();
            event.setTitle(" ");

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }
    }

    @Nested
    public class EventDescription {

        @Test
        public void shouldFailEventWithTooLongDescription() {
            EventDto event = TestData.event().dto();
            event.setDescription(event.getDescription().repeat(1000));

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullDescription() {
            EventDto event = TestData.event().dto();
            event.setDescription(null);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithEmptyDescription() {
            EventDto event = TestData.event().dto();
            event.setDescription("");

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithWhitespaceDescription() {
            EventDto event = TestData.event().dto();
            event.setDescription(" ");

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

    }

    @Nested
    public class EventLocalDateTime {

        @Test
        public void shouldFailEventWithPastLocalDateTime() {
            EventDto event = TestData.event().dto();
            LocalDate today = LocalDate.now();
            event.setLocalDate(today);
            LocalTime pastTime = LocalTime.now().minus(1, ChronoUnit.HOURS);
            event.setLocalTime(pastTime);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullLocalDate() {
            EventDto event = TestData.event().dto();
            event.setLocalDate(null);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullLocalTime() {
            EventDto event = TestData.event().dto();
            event.setLocalTime(null);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullTimezone() {
            EventDto event = TestData.event().dto();
            event.setTimezone(null);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }
    }

}