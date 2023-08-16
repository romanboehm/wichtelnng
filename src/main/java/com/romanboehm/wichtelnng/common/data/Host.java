package com.romanboehm.wichtelnng.common.data;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@Embeddable
public record Host(
    @NotBlank
    @Length(max = 100)
    String name,
    @NotBlank
    @Length(max = 255)
    String email
) {}
