package com.romanboehm.wichtelnng.common.data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Embeddable
public class MonetaryAmount {

    @Column(precision = 10, scale = 2)
    @NotNull
    @DecimalMin("0")
    private BigDecimal number;

    @NotBlank
    @Length(max = 3)
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
