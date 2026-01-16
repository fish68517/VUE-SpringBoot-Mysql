package com.postgraduate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new major.
 * Contains required fields for major creation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MajorCreateRequest {

    @NotBlank(message = "Major name cannot be blank")
    private String name;

    private String direction;

}
