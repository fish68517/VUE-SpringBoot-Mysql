package com.travelMemory.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

/**
 * Validator for date range constraints.
 * Validates that start date is before or equal to end date.
 */
public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

    private String startDateField;
    private String endDateField;

    @Override
    public void initialize(ValidDateRange annotation) {
        this.startDateField = annotation.startDateField();
        this.endDateField = annotation.endDateField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            LocalDate startDate = getFieldValue(value, startDateField, LocalDate.class);
            LocalDate endDate = getFieldValue(value, endDateField, LocalDate.class);

            if (startDate == null || endDate == null) {
                return true;
            }

            if (startDate.isAfter(endDate)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "Start date must be before or equal to end date")
                        .addPropertyNode(startDateField)
                        .addConstraintViolation();
                return false;
            }

            return true;
        } catch (Exception e) {
            return true;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getFieldValue(Object object, String fieldName, Class<T> type) throws Exception {
        java.lang.reflect.Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(object);
    }
}
