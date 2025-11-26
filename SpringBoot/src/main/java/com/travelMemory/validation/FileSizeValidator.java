package com.travelMemory.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for file size constraints.
 * Validates that file size does not exceed the maximum allowed size.
 */
public class FileSizeValidator implements ConstraintValidator<ValidFileSize, Long> {

    private long maxSizeInBytes;

    @Override
    public void initialize(ValidFileSize annotation) {
        // Convert MB to bytes (1 MB = 1024 * 1024 bytes)
        this.maxSizeInBytes = (long) annotation.maxSizeInMB() * 1024 * 1024;
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (value > maxSizeInBytes) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    String.format("File size must not exceed %d MB", maxSizeInBytes / (1024 * 1024)))
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
