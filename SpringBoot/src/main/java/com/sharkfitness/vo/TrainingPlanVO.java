package com.sharkfitness.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Value object for TrainingPlan responses
 */
@Data
public class TrainingPlanVO {
    
    private Long id;
    private String name;
    private String description;
    private String exercises;  // JSON string of exercise details
    private Long coachId;
    private String coachName;
    private Long studentId;
    private String studentName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
