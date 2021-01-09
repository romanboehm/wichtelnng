package com.romanboehm.wichtelnng.validation;

import com.romanboehm.wichtelnng.TestData;
import com.romanboehm.wichtelnng.model.Participant;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ParticipantValidationTest extends BaseValidationTest {

    @Test
    public void shouldAcceptValidParticipant() {
        Participant participant = TestData.participant();

        Assertions.assertThat(getValidator().validate(participant)).isEmpty();
    }

    @Nested
    public class ParticipantName {

        @Test
        public void shouldFailParticipantWithTooLongName() {
            Participant participant = TestData.participant();
            participant.setName(participant.getName().repeat(20));

            Assertions.assertThat(getValidator().validate(participant)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithEmptyName() {
            Participant participant = TestData.participant();
            participant.setName("");

            Assertions.assertThat(getValidator().validate(participant)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithWhitespaceName() {
            Participant participant = TestData.participant();
            participant.setName(" ");

            Assertions.assertThat(getValidator().validate(participant)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithNullName() {
            Participant participant = TestData.participant();
            participant.setName(null);

            Assertions.assertThat(getValidator().validate(participant)).isNotEmpty();
        }
    }

    @Nested
    public class ParticipantEmail {
        @Test
        public void shouldFailParticipantWithInvalidEmail() {
            Participant participant = TestData.participant();
            participant.setEmail("not an email address");

            Assertions.assertThat(getValidator().validate(participant)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithEmptyEmail() {
            Participant participant = TestData.participant();
            participant.setEmail("");

            Assertions.assertThat(getValidator().validate(participant)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithWhitespaceEmail() {
            Participant participant = TestData.participant();
            participant.setEmail(" ");

            Assertions.assertThat(getValidator().validate(participant)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithNullEmail() {
            Participant participant = TestData.participant();
            participant.setEmail(null);

            Assertions.assertThat(getValidator().validate(participant)).isNotEmpty();
        }
    }

}