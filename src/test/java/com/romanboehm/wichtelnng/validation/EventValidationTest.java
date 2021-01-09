package com.romanboehm.wichtelnng.validation;

import com.romanboehm.wichtelnng.TestData;
import com.romanboehm.wichtelnng.model.Event;
import com.romanboehm.wichtelnng.model.Host;
import com.romanboehm.wichtelnng.model.MonetaryAmount;
import com.romanboehm.wichtelnng.model.Participant;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EventValidationTest extends BaseValidationTest {

    @Test
    public void shouldAcceptValidEvent() {
        Event event = TestData.event().asObject();

        Assertions.assertThat(getValidator().validate(event)).isEmpty();
    }

    @Nested
    public class EventPlace {

        @Test
        public void shouldFailEventWithTooLongPlace() {
            Event event = TestData.event().asObject();
            event.setPlace(event.getPlace().repeat(100));

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullPlace() {
            Event event = TestData.event().asObject();
            event.setPlace(null);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithEmptyPlace() {
            Event event = TestData.event().asObject();
            event.setPlace("");

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithWhitespacePlace() {
            Event event = TestData.event().asObject();
            event.setPlace(" ");

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }
    }

    @Nested
    public class EventDependencies {

        @Test
        public void shouldFailEventWithInvalidMonetaryAmount() {
            Event event = TestData.event().asObject();
            MonetaryAmount monetaryAmount = new MonetaryAmount();
            event.setMonetaryAmount(monetaryAmount);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullMonetaryAmount() {
            Event event = TestData.event().asObject();
            event.setMonetaryAmount(null);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullHost() {
            Event event = TestData.event().asObject();
            event.setHost(null);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithInvalidHost() {
            Event event = TestData.event().asObject();
            event.setHost(new Host());

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullParticipants() {
            Event event = TestData.event().asObject();
            event.setParticipants(null);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithLessThanThreeParticipants() {
            Event event = TestData.event().asObject();
            List<Participant> oneParticipantTooFew = IntStream.rangeClosed(1, 2)
                    .mapToObj(value -> {
                        Participant participant = new Participant();
                        participant.setName("Name" + value);
                        participant.setEmail("Email@" + value);
                        return participant;
                    }).collect(Collectors.toList());
            event.setParticipants(oneParticipantTooFew);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithMoreThanOneHundredParticipants() {
            Event event = TestData.event().asObject();
            List<Participant> oneParticipantTooMany = IntStream.rangeClosed(1, 101)
                    .mapToObj(value -> {
                        Participant participant = new Participant();
                        participant.setName("Name" + value);
                        participant.setEmail("Email@" + value);
                        return participant;
                    }).collect(Collectors.toList());
            event.setParticipants(oneParticipantTooMany);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithInvalidParticipant() {
            Event event = TestData.event().asObject();
            Participant participant = new Participant();
            participant.setName(null);
            participant.setEmail(null);
            List<Participant> participants = new ArrayList<>();
            participants.add(participant);
            participants.addAll(event.getParticipants().subList(1, event.getParticipants().size() - 1));
            event.setParticipants(participants);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

    }

    @Nested
    public class EventTitle {

        @Test
        public void shouldFailEventWithTooLongTitle() {
            Event event = TestData.event().asObject();
            event.setTitle(event.getTitle().repeat(100));

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullTitle() {
            Event event = TestData.event().asObject();
            event.setTitle(null);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithEmptyTitle() {
            Event event = TestData.event().asObject();
            event.setTitle("");

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithWhitespaceTitle() {
            Event event = TestData.event().asObject();
            event.setTitle(" ");

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }
    }

    @Nested
    public class EventDescription {

        @Test
        public void shouldFailEventWithTooLongDescription() {
            Event event = TestData.event().asObject();
            event.setDescription(event.getDescription().repeat(1000));

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullDescription() {
            Event event = TestData.event().asObject();
            event.setDescription(null);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithEmptyDescription() {
            Event event = TestData.event().asObject();
            event.setDescription("");

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithWhitespaceDescription() {
            Event event = TestData.event().asObject();
            event.setDescription(" ");

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

    }

    @Nested
    public class EventLocalDateTime {

        @Test
        public void shouldFailEventWithPastLocalDateTime() {
            Event event = TestData.event().asObject();
            LocalDate today = LocalDate.now();
            event.setLocalDate(today);
            LocalTime pastTime = LocalTime.now().minus(1, ChronoUnit.HOURS);
            event.setLocalTime(pastTime);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullLocalDate() {
            Event event = TestData.event().asObject();
            event.setLocalDate(null);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }

        @Test
        public void shouldFailEventWithNullLocalTime() {
            Event event = TestData.event().asObject();
            event.setLocalTime(null);

            Assertions.assertThat(getValidator().validate(event)).isNotEmpty();
        }
    }

}