package com.romanboehm.wichtelnng.model.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class MonetaryAmount {
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal number;

    @Column(nullable = false, length = 3)
    private String currency;

    public BigDecimal getNumber() {
        return number;
    }

    public MonetaryAmount setNumber(BigDecimal number) {
        this.number = number;
        return this;
    }

    public String getCurrency() {
        return currency;
    }

    public MonetaryAmount setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public String toString() {
        return String.format("Event.MonetaryAmount(number=%s, currency=%s)", this.getNumber(), this.getCurrency());
    }
}
