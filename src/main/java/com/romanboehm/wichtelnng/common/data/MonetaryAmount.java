package com.romanboehm.wichtelnng.common.data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Currency;

@Embeddable
public record MonetaryAmount(
    @Column(precision = 10, scale = 2)
    @NotNull
    @DecimalMin("0")
    BigDecimal number,
    @NotNull
    Currency currency
){}
