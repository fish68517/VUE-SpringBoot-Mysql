package com.sharkfitness.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * DTO for creating or updating training plans
 */
@Data
public class TrainingPlanRequest {
    
    @NotBlank(message = "Plan name is required")
    @Size(max = 200, message = "Plan name must not exceed 200 characters")
    private String name;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @NotBlank(message = "Exercises are required")
    private String exercises;  // JSON string of exercise details
    
    @NotNull(message = "Student ID is required")
    private Long studentId;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private String status;  // active, completed, cancelled
}
