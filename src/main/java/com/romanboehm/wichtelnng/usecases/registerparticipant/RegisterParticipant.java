package com.romanboehm.wichtelnng.usecases.registerparticipant;

import com.romanboehm.wichtelnng.data.Event;
import com.romanboehm.wichtelnng.data.MonetaryAmount;
import lombok.Data;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

// Class may be package-private, but properties (i.e. getters) need be public for validator.
@Data
class RegisterParticipant {

    @NotNull
    private UUID id;

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    @Size(max = 1000)
    private String description;

    @NotNull
    @Min(0)
    private BigDecimal number;

    @NotNull
    private CurrencyUnit currency;

    @NotBlank
    @Size(max = 100)
    private String hostName;

    @NotBlank
    @Email
    private String hostEmail;

    @NotBlank
    @Size(max = 100)
    private String participantName;

    @NotBlank
    @Email
    private String participantEmail;

    static RegisterParticipant registerFor(Event event) {
        return new RegisterParticipant()
                .setId(event.getId())
                .setTitle(event.getTitle())
                .setDescription(event.getDescription())
                .setCurrency(Monetary.getCurrency(event.getMonetaryAmount().getCurrency()))
                .setNumber(event.getMonetaryAmount().getNumber())
                .setHostName(event.getHost().getName())
                .setHostEmail(event.getHost().getEmail());
    }

    public MonetaryAmount getMonetaryAmount() {
        return new MonetaryAmount()
                .setNumber(number)
                .setCurrency(currency.getCurrencyCode());
    }
}
