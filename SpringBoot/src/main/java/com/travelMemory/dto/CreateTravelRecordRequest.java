package com.travelMemory.dto;

import com.travelMemory.validation.ValidDateRange;
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
@ValidDateRange(startDateField = "startDate", endDateField = "endDate")
public class CreateTravelRecordRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    private String title;

    @NotBlank(message = "Destination is required")
    @Size(min = 1, max = 200, message = "Destination must be between 1 and 200 characters")
    private String destination;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private String diaryContent;

    private Boolean isPublic;
}
