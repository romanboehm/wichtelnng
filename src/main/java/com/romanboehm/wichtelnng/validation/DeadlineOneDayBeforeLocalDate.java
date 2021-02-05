package com.romanboehm.wichtelnng.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validation annotation to validate that two dates are at least a day apart.
 */
@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = OneDayBeforeValidator.class)
@Documented
public @interface DeadlineOneDayBeforeLocalDate {
    String message() default "{constraints.fieldmatch}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
