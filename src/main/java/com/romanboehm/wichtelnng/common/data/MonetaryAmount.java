package com.romanboehm.wichtelnng.common.data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

@Embeddable
public class MonetaryAmount {

    @Column(precision = 10, scale = 2)
    @NotNull
    @DecimalMin("0")
    private BigDecimal number;

    @NotNull
    private Currency currency;

    public MonetaryAmount() {
    }

    public BigDecimal getNumber() {
        return this.number;
    }

    public MonetaryAmount setNumber(BigDecimal number) {
        this.number = number;
        return this;
    }

    public Currency getCurrency() {
        return this.currency;
    }

    public MonetaryAmount setCurrency(Currency currency) {
        this.currency = currency;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MonetaryAmount that = (MonetaryAmount) o;
        return Objects.equals(number, that.number) && Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, currency);
    }

    @Override
    public String toString() {
        return "MonetaryAmount{" +
                "number=" + number +
                ", currency='" + currency + '\'' +
                '}';
    }
}
