package com.romanboehm.wichtelnng.validation;

import com.romanboehm.wichtelnng.TestData;
import com.romanboehm.wichtelnng.model.dto.HostDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class HostValidationTest extends BaseValidationTest {

    @Test
    public void shouldAcceptValidHost() {
        HostDto host = TestData.host();

        Assertions.assertThat(getValidator().validate(host)).isEmpty();
    }

    @Nested
    public class HostName {
        @Test
        public void shouldFailHostWithTooLongName() {
            HostDto host = TestData.host();
            host.setName(host.getName().repeat(20));

            Assertions.assertThat(getValidator().validate(host)).isNotEmpty();
        }

        @Test
        public void shouldFailHostWithEmptyName() {
            HostDto host = TestData.host();
            host.setName("");

            Assertions.assertThat(getValidator().validate(host)).isNotEmpty();
        }

        @Test
        public void shouldFailHostWithWhitespaceName() {
            HostDto host = TestData.host();
            host.setName(" ");

            Assertions.assertThat(getValidator().validate(host)).isNotEmpty();
        }

        @Test
        public void shouldFailHostWithNullName() {
            HostDto host = TestData.host();
            host.setName(null);

            Assertions.assertThat(getValidator().validate(host)).isNotEmpty();
        }
    }

    @Nested
    public class HostEmail {
        @Test
        public void shouldFailHostWithInvalidEmail() {
            HostDto host = TestData.host();
            host.setEmail("not an email address");

            Assertions.assertThat(getValidator().validate(host)).isNotEmpty();
        }

        @Test
        public void shouldFailHostWithEmptyEmail() {
            HostDto host = TestData.host();
            host.setEmail("");

            Assertions.assertThat(getValidator().validate(host)).isNotEmpty();
        }

        @Test
        public void shouldFailHostWithWhitespaceEmail() {
            HostDto host = TestData.host();
            host.setEmail(" ");

            Assertions.assertThat(getValidator().validate(host)).isNotEmpty();
        }

        @Test
        public void shouldFailHostWithNullEmail() {
            HostDto host = TestData.host();
            host.setEmail(null);

            Assertions.assertThat(getValidator().validate(host)).isNotEmpty();
        }
    }


}