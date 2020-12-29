package com.romanboehm.wichtelnng.validation;

import com.romanboehm.wichtelnng.TestData;
import com.romanboehm.wichtelnng.model.MonetaryAmount;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class MonetaryAmountValidationTest extends BaseValidationTest {

    @Nested
    public class MonetaryAmountNumber {
        @Test
        public void shouldFailMonetaryAmountWithNegativeNumber() {
            MonetaryAmount monetaryAmount = TestData.monetaryAmount();
            monetaryAmount.setNumber(BigDecimal.valueOf(-1));

            assertThat(getValidator().validate(monetaryAmount)).isNotEmpty();
        }

        @Test
        public void shouldFailMonetaryAmountWithNullNumber() {
            MonetaryAmount monetaryAmount = TestData.monetaryAmount();
            monetaryAmount.setNumber(null);

            assertThat(getValidator().validate(monetaryAmount)).isNotEmpty();
        }
    }

    @Nested
    public class MonetaryAmountCurrency {
        @Test
        public void shouldFailMonetaryAmountWithNullCurrency() {
            MonetaryAmount monetaryAmount = TestData.monetaryAmount();
            monetaryAmount.setCurrency(null);

            assertThat(getValidator().validate(monetaryAmount)).isNotEmpty();
        }
    }


}