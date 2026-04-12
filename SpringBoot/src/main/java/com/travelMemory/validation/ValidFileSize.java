package com.travelMemory.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for validating file size.
 * Ensures that file size does not exceed the maximum allowed size.
 *
 * Usage:
 * @ValidFileSize(maxSizeInMB = 500)
 * private Long fileSize;
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileSizeValidator.class)
public @interface ValidFileSize {

    String message() default "File size exceeds maximum allowed size";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Maximum file size in megabytes
     */
    int maxSizeInMB() default 500;
}
