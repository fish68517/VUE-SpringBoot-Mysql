package com.travelMemory.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLikeRequest {

    @NotNull(message = "Travel record ID is required")
    private Long travelRecordId;
}
