package com.romanboehm.wichtelnng.model.entity;

import lombok.Data;
import org.javamoney.moneta.Money;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Data
@Embeddable
public class MonetaryAmount {
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal number;

    @Column(nullable = false, length = 3)
    private String currency;

    @Override
    public String toString() {
        return Money.of(number, currency).toString();
    }
}
