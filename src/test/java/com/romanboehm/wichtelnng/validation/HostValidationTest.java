package com.romanboehm.wichtelnng.validation;

import com.romanboehm.wichtelnng.TestData;
import com.romanboehm.wichtelnng.model.Host;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class HostValidationTest extends BaseValidationTest {

    @Test
    public void shouldAcceptValidHost() {
        Host host = TestData.host();

        Assertions.assertThat(getValidator().validate(host)).isEmpty();
    }

    @Nested
    public class HostName {
        @Test
        public void shouldFailHostWithTooLongName() {
            Host host = TestData.host();
            host.setName(host.getName().repeat(20));

            Assertions.assertThat(getValidator().validate(host)).isNotEmpty();
        }

        @Test
        public void shouldFailHostWithEmptyName() {
            Host host = TestData.host();
            host.setName("");

            Assertions.assertThat(getValidator().validate(host)).isNotEmpty();
        }

        @Test
        public void shouldFailHostWithWhitespaceName() {
            Host host = TestData.host();
            host.setName(" ");

            Assertions.assertThat(getValidator().validate(host)).isNotEmpty();
        }

        @Test
        public void shouldFailHostWithNullName() {
            Host host = TestData.host();
            host.setName(null);

            Assertions.assertThat(getValidator().validate(host)).isNotEmpty();
        }
    }

    @Nested
    public class HostEmail {
        @Test
        public void shouldFailHostWithInvalidEmail() {
            Host host = TestData.host();
            host.setEmail("not an email address");

            Assertions.assertThat(getValidator().validate(host)).isNotEmpty();
        }

        @Test
        public void shouldFailHostWithEmptyEmail() {
            Host host = TestData.host();
            host.setEmail("");

            Assertions.assertThat(getValidator().validate(host)).isNotEmpty();
        }

        @Test
        public void shouldFailHostWithWhitespaceEmail() {
            Host host = TestData.host();
            host.setEmail(" ");

            Assertions.assertThat(getValidator().validate(host)).isNotEmpty();
        }

        @Test
        public void shouldFailHostWithNullEmail() {
            Host host = TestData.host();
            host.setEmail(null);

            Assertions.assertThat(getValidator().validate(host)).isNotEmpty();
        }
    }


}