package com.sharkfitness.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


import java.time.LocalDate;

@Data
public class DietRecordRequest {
    
    @NotBlank(message = "Meal type is required")
    private String mealType;  // breakfast, lunch, dinner, snack
    
    @NotBlank(message = "Food items are required")
    private String foodItems;
    
    @NotNull(message = "Calories are required")
    private Integer calories;
    
    @NotNull(message = "Meal date is required")
    private LocalDate mealDate;
}
