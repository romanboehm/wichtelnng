package com.romanboehm.wichtelnng.validation;

import com.romanboehm.wichtelnng.TestData;
import com.romanboehm.wichtelnng.model.dto.ParticipantDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ParticipantValidationTest extends BaseValidationTest {

    @Test
    public void shouldAcceptValidParticipant() {
        ParticipantDto participant = TestData.participant().dto();

        Assertions.assertThat(getValidator().validate(participant)).isEmpty();
    }

    @Nested
    public class ParticipantName {

        @Test
        public void shouldFailParticipantWithTooLongName() {
            ParticipantDto participant = TestData.participant().dto();
            participant.setName(participant.getName().repeat(20));

            Assertions.assertThat(getValidator().validate(participant)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithEmptyName() {
            ParticipantDto participant = TestData.participant().dto();
            participant.setName("");

            Assertions.assertThat(getValidator().validate(participant)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithWhitespaceName() {
            ParticipantDto participant = TestData.participant().dto();
            participant.setName(" ");

            Assertions.assertThat(getValidator().validate(participant)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithNullName() {
            ParticipantDto participant = TestData.participant().dto();
            participant.setName(null);

            Assertions.assertThat(getValidator().validate(participant)).isNotEmpty();
        }
    }

    @Nested
    public class ParticipantEmail {
        @Test
        public void shouldFailParticipantWithInvalidEmail() {
            ParticipantDto participant = TestData.participant().dto();
            participant.setEmail("not an email address");

            Assertions.assertThat(getValidator().validate(participant)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithEmptyEmail() {
            ParticipantDto participant = TestData.participant().dto();
            participant.setEmail("");

            Assertions.assertThat(getValidator().validate(participant)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithWhitespaceEmail() {
            ParticipantDto participant = TestData.participant().dto();
            participant.setEmail(" ");

            Assertions.assertThat(getValidator().validate(participant)).isNotEmpty();
        }

        @Test
        public void shouldFailParticipantWithNullEmail() {
            ParticipantDto participant = TestData.participant().dto();
            participant.setEmail(null);

            Assertions.assertThat(getValidator().validate(participant)).isNotEmpty();
        }
    }

}