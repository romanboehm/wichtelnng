package com.romanboehm.wichtelnng.validation;

import com.romanboehm.wichtelnng.TestData;
import com.romanboehm.wichtelnng.model.dto.EventDto;
import com.romanboehm.wichtelnng.model.dto.HostDto;
import com.romanboehm.wichtelnng.model.dto.MonetaryAmountDto;
import com.romanboehm.wichtelnng.model.dto.ParticipantDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class EventValidationTest extends BaseValidationTest {

    @Test
    public void shouldAcceptValidEvent() {
        EventDto event = TestData.event().asDto();

        Assertions.assertThat(getValidator().validate(event)).isEmpty();
    }

    @Nested
    public class EventPlace {

        @Test
        public void shouldFailEventWithTooLongPlace() {
            EventDto event = TestData.event().asDto();
            event.setPlace(event.getPlace().repeat(100));

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullPlace() {
            EventDto event = TestData.event().asDto();
            event.setPlace(null);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithEmptyPlace() {
            EventDto event = TestData.event().asDto();
            event.setPlace("");

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithWhitespacePlace() {
            EventDto event = TestData.event().asDto();
            event.setPlace(" ");

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }
    }

    @Nested
    public class EventDependencies {

        @Test
        public void shouldFailEventWithInvalidMonetaryAmount() {
            EventDto event = TestData.event().asDto();
            MonetaryAmountDto monetaryAmount = new MonetaryAmountDto();
            event.setMonetaryAmount(monetaryAmount);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullMonetaryAmount() {
            EventDto event = TestData.event().asDto();
            event.setMonetaryAmount(null);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullHost() {
            EventDto event = TestData.event().asDto();
            event.setHost(null);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithInvalidHost() {
            EventDto event = TestData.event().asDto();
            event.setHost(new HostDto());

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullParticipants() {
            EventDto event = TestData.event().asDto();
            event.setParticipants(null);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithInvalidParticipant() {
            EventDto event = TestData.event().asDto();
            ParticipantDto participant = new ParticipantDto();
            participant.setName(null);
            participant.setEmail(null);
            List<ParticipantDto> participants = new ArrayList<>();
            participants.add(participant);
            event.setParticipants(participants);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

    }

    @Nested
    public class EventTitle {

        @Test
        public void shouldFailEventWithTooLongTitle() {
            EventDto event = TestData.event().asDto();
            event.setTitle(event.getTitle().repeat(100));

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullTitle() {
            EventDto event = TestData.event().asDto();
            event.setTitle(null);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithEmptyTitle() {
            EventDto event = TestData.event().asDto();
            event.setTitle("");

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithWhitespaceTitle() {
            EventDto event = TestData.event().asDto();
            event.setTitle(" ");

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }
    }

    @Nested
    public class EventDescription {

        @Test
        public void shouldFailEventWithTooLongDescription() {
            EventDto event = TestData.event().asDto();
            event.setDescription(event.getDescription().repeat(1000));

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullDescription() {
            EventDto event = TestData.event().asDto();
            event.setDescription(null);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithEmptyDescription() {
            EventDto event = TestData.event().asDto();
            event.setDescription("");

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithWhitespaceDescription() {
            EventDto event = TestData.event().asDto();
            event.setDescription(" ");

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

    }

    @Nested
    public class EventLocalDateTime {

        @Test
        public void shouldFailEventWithPastLocalDateTime() {
            EventDto event = TestData.event().asDto();
            LocalDate today = LocalDate.now();
            event.setLocalDate(today);
            LocalTime pastTime = LocalTime.now().minus(1, ChronoUnit.HOURS);
            event.setLocalTime(pastTime);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullLocalDate() {
            EventDto event = TestData.event().asDto();
            event.setLocalDate(null);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullLocalTime() {
            EventDto event = TestData.event().asDto();
            event.setLocalTime(null);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }
    }

}