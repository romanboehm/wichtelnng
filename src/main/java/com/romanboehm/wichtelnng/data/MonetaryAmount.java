package com.romanboehm.wichtelnng.data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

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

    public MonetaryAmount setNumber(BigDecimal number) {
        this.number = number;
        return this;
    }

    public String getCurrency() {
        return this.currency;
    }

    public MonetaryAmount setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    @Override
    public String toString() {
        return "MonetaryAmount{" +
                "number=" + number +
                ", currency='" + currency + '\'' +
                '}';
    }
}
