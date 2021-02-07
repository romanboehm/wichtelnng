package com.romanboehm.wichtelnng.model.dto;

import javax.money.CurrencyUnit;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class MonetaryAmountDto {

    @NotNull
    @Min(0)
    private BigDecimal number;

    @NotNull
    private CurrencyUnit currency;

    public BigDecimal getNumber() {
        return number;
    }

    public MonetaryAmountDto setNumber(BigDecimal number) {
        this.number = number;
        return this;
    }

    public CurrencyUnit getCurrency() {
        return currency;
    }

    public MonetaryAmountDto setCurrency(CurrencyUnit currency) {
        this.currency = currency;
        return this;
    }

    public String toString() {
        return String.format("MonetaryAmountDto(number=%s, currency=%s)", this.getNumber(), this.getCurrency());
    }
}
