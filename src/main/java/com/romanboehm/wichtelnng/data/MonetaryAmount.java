package com.romanboehm.wichtelnng.data;

import org.javamoney.moneta.Money;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class MonetaryAmount {

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal number;

    @Column(nullable = false, length = 3)
    private String currency;

    public MonetaryAmount() {
    }

    public BigDecimal getNumber() {
        return this.number;
    }

    public String getCurrency() {
        return this.currency;
    }

    public MonetaryAmount setNumber(BigDecimal number) {
        this.number = number;
        return this;
    }

    public MonetaryAmount setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    // FIXME: This is a workaround as long as long as we still (mis-) use the `@Embeddable` classes for display purposes.
    public String getDisplayString() {
        return Money.of(number, currency).toString();
    }

    @Override
    public String toString() {
        return "MonetaryAmount{" +
                "number=" + number +
                ", currency='" + currency + '\'' +
                '}';
    }
}
