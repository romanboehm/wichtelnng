package com.romanboehm.wichtelnng.validation;

import com.romanboehm.wichtelnng.TestData;
import com.romanboehm.wichtelnng.model.Host;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HostValidationTest extends BaseValidationTest {

    @Test
    public void shouldAcceptValidHost() {
        Host host = TestData.host();

        assertThat(getValidator().validate(host)).isEmpty();
    }

    @Nested
    public class HostName {
        @Test
        public void shouldFailHostWithTooLongName() {
            Host host = TestData.host();
            host.setName(host.getName().repeat(20));

            assertThat(getValidator().validate(host)).isNotEmpty();
        }

        @Test
        public void shouldFailHostWithEmptyName() {
            Host host = TestData.host();
            host.setName("");

            assertThat(getValidator().validate(host)).isNotEmpty();
        }

        @Test
        public void shouldFailHostWithWhitespaceName() {
            Host host = TestData.host();
            host.setName(" ");

            assertThat(getValidator().validate(host)).isNotEmpty();
        }

        @Test
        public void shouldFailHostWithNullName() {
            Host host = TestData.host();
            host.setName(null);

            assertThat(getValidator().validate(host)).isNotEmpty();
        }
    }

    @Nested
    public class HostEmail {
        @Test
        public void shouldFailHostWithInvalidEmail() {
            Host host = TestData.host();
            host.setEmail("not an email address");

            assertThat(getValidator().validate(host)).isNotEmpty();
        }

        @Test
        public void shouldFailHostWithEmptyEmail() {
            Host host = TestData.host();
            host.setEmail("");

            assertThat(getValidator().validate(host)).isNotEmpty();
        }

        @Test
        public void shouldFailHostWithWhitespaceEmail() {
            Host host = TestData.host();
            host.setEmail(" ");

            assertThat(getValidator().validate(host)).isNotEmpty();
        }

        @Test
        public void shouldFailHostWithNullEmail() {
            Host host = TestData.host();
            host.setEmail(null);

            assertThat(getValidator().validate(host)).isNotEmpty();
        }
    }


}