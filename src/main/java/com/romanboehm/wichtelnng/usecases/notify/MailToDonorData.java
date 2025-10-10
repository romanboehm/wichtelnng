package com.romanboehm.wichtelnng.usecases.notify;

import com.romanboehm.wichtelnng.common.data.Event;
import com.romanboehm.wichtelnng.common.data.Host;
import org.javamoney.moneta.Money;

import javax.money.MonetaryContext;
import javax.money.MonetaryContextBuilder;
import java.math.BigDecimal;
import java.util.Currency;

record MailToDonorData(
        Match match,
        String title,
        String description,
        Host host,
        String price
) {


    static MailToDonorData from(Event event, Match match) {
        return new MailToDonorData(
                match,
                event.getTitle(),
                event.getDescription(),
                event.getHost(),
                createMoneyStringWithFixedDecimals(
                        event.getMonetaryAmount().number(),
                        event.getMonetaryAmount().currency())
        );
    }

    private static final MonetaryContext TWO_DECIMALS_MONETARY_CTX = MonetaryContextBuilder.of()
            .setMaxScale(2)
            .setFixedScale(true)
            .build();

    private static String createMoneyStringWithFixedDecimals(BigDecimal number, Currency currency) {
        return Money.of(number, currency.getCurrencyCode(), TWO_DECIMALS_MONETARY_CTX).toString();
    }
}
