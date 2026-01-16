package com.postgraduate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating an existing school.
 * Contains all fields that can be updated with validation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolUpdateRequest {

    @NotBlank(message = "School name is required")
    @Size(min = 1, max = 255, message = "School name must be between 1 and 255 characters")
    private String name;

    @NotBlank(message = "City is required")
    @Size(min = 1, max = 100, message = "City must be between 1 and 100 characters")
    private String city;

    @NotBlank(message = "School tier is required")
    @Size(min = 1, max = 50, message = "School tier must be between 1 and 50 characters")
    private String tier;

    @Size(max = 255, message = "Website must not exceed 255 characters")
    private String website;

    @Size(max = 65535, message = "Introduction must not exceed 65535 characters")
    private String intro;

}
