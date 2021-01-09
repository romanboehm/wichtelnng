package com.romanboehm.wichtelnng.validation;

import com.romanboehm.wichtelnng.TestData;
import com.romanboehm.wichtelnng.model.MonetaryAmount;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class MonetaryAmountValidationTest extends BaseValidationTest {

    @Nested
    public class MonetaryAmountNumber {
        @Test
        public void shouldFailMonetaryAmountWithNegativeNumber() {
            MonetaryAmount monetaryAmount = TestData.monetaryAmount();
            monetaryAmount.setNumber(BigDecimal.valueOf(-1));

            Assertions.assertThat(getValidator().validate(monetaryAmount)).isNotEmpty();
        }

        @Test
        public void shouldFailMonetaryAmountWithNullNumber() {
            MonetaryAmount monetaryAmount = TestData.monetaryAmount();
            monetaryAmount.setNumber(null);

            Assertions.assertThat(getValidator().validate(monetaryAmount)).isNotEmpty();
        }
    }

    @Nested
    public class MonetaryAmountCurrency {
        @Test
        public void shouldFailMonetaryAmountWithNullCurrency() {
            MonetaryAmount monetaryAmount = TestData.monetaryAmount();
            monetaryAmount.setCurrency(null);

            Assertions.assertThat(getValidator().validate(monetaryAmount)).isNotEmpty();
        }
    }


}