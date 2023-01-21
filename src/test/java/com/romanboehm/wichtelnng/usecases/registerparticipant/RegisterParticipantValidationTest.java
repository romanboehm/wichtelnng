package com.romanboehm.wichtelnng.usecases.registerparticipant;

import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.romanboehm.wichtelnng.usecases.registerparticipant.RegisterParticipantTestData.participantRegistration;
import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.assertj.core.api.Assertions.assertThat;

class RegisterParticipantValidationTest {

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
