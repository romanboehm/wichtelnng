package com.romanboehm.wichtelnng.model.dto;

import com.romanboehm.wichtelnng.model.entity.Event;
import com.romanboehm.wichtelnng.model.entity.MonetaryAmount;
import lombok.Data;
import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ParticipantRegistration {

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

    public static ParticipantRegistration with(Event event) {
        return new ParticipantRegistration()
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
