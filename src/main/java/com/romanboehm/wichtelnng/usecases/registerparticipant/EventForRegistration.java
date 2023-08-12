package com.romanboehm.wichtelnng.usecases.registerparticipant;

import com.romanboehm.wichtelnng.common.data.Event;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Currency;

record EventForRegistration(
                    @NotBlank
                    @Size(max = 100)
                    String title,
                    @NotBlank
                    @Size(max = 1000)
                    String description,
                    @NotNull
                    @Min(0)
                    BigDecimal number,
                    @NotNull
                    Currency currency,
                    @NotBlank
                    @Size(max = 100)
                    String hostName,
                    @NotBlank
                    @Email
                    String hostEmail
) {
    static EventForRegistration from(Event event) {
        return new EventForRegistration(
                            event.getTitle(),
                            event.getDescription(),
                            event.getMonetaryAmount().getNumber(),
                            event.getMonetaryAmount().getCurrency(),
                            event.getHost().getName(),
                            event.getHost().getEmail()
        );
    }
}
