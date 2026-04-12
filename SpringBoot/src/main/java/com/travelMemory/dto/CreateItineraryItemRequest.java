package com.travelMemory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateItineraryItemRequest {

    @NotNull(message = "Item date is required")
    private LocalDate itemDate;

    @NotBlank(message = "Item type is required")
    @Size(min = 1, max = 50, message = "Item type must be between 1 and 50 characters")
    private String itemType;

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    private String title;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Size(max = 200, message = "Location must not exceed 200 characters")
    private String location;
}
