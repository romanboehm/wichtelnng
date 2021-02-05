package com.romanboehm.wichtelnng.validation;

import com.romanboehm.wichtelnng.model.dto.EventDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OneDayBeforeValidator implements ConstraintValidator<DeadlineOneDayBeforeLocalDate, EventDto> {

    @Override
    public boolean isValid(EventDto value, ConstraintValidatorContext context) {
        if (value.getDeadline() == null || value.getLocalDate() == null) {
            return true; // This case is handled by `@NotNull` validators.
        }
        boolean isValid = value.getDeadline().isBefore(value.getLocalDate());
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate("Must be at least a day before the event is taking place")
                    .addPropertyNode("deadline").addConstraintViolation();
        }
        return isValid;
    }
}
