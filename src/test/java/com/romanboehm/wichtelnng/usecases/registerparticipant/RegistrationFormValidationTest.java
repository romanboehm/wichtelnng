package com.romanboehm.wichtelnng.usecases.registerparticipant;

import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.romanboehm.wichtelnng.usecases.registerparticipant.RegisterParticipantTestData.registrationForm;
import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.assertj.core.api.Assertions.assertThat;

class RegistrationFormValidationTest {

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
        var form = registrationForm()
                .setParticipantName("foo".repeat(100));

        assertThat(validator.validate(form)).isNotEmpty();
    }

    @Test
    void shouldFailParticipantWithEmptyName() {
        var form = registrationForm()
                .setParticipantName("");

        assertThat(validator.validate(form)).isNotEmpty();
    }

    @Test
    void shouldFailParticipantWithWhitespaceName() {
        var form = registrationForm()
                .setParticipantName(" ");

        assertThat(validator.validate(form)).isNotEmpty();
    }

    @Test
    void shouldFailParticipantWithNullName() {
        var form = registrationForm()
                .setParticipantName(null);

        assertThat(validator.validate(form)).isNotEmpty();
    }

    @Test
    void shouldFailParticipantWithInvalidEmail() {
        var form = registrationForm()
                .setParticipantEmail("notavalid.email");

        assertThat(validator.validate(form)).isNotEmpty();
    }

    @Test
    void shouldFailParticipantWithEmptyEmail() {
        var form = registrationForm()
                .setParticipantEmail("");

        assertThat(validator.validate(form)).isNotEmpty();
    }

    @Test
    void shouldFailParticipantWithWhitespaceEmail() {
        var form = registrationForm()
                .setParticipantEmail(" ");

        assertThat(validator.validate(form)).isNotEmpty();
    }

    @Test
    void shouldFailParticipantWithNullEmail() {
        var form = registrationForm()
                .setParticipantEmail(null);

        assertThat(validator.validate(form)).isNotEmpty();
    }
}
