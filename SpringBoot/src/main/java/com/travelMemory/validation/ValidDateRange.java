package com.travelMemory.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for validating date ranges.
 * Ensures that start date is before or equal to end date.
 *
 * Usage:
 * @ValidDateRange(startDateField = "startDate", endDateField = "endDate")
 * public class MyRequest { ... }
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
public @interface ValidDateRange {

    String message() default "Start date must be before or equal to end date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * The name of the start date field
     */
    String startDateField();

    /**
     * The name of the end date field
     */
    String endDateField();
}
