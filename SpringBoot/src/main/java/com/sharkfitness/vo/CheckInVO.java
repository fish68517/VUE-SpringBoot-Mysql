package com.sharkfitness.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Value object for check-in data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckInVO {
    
    private Long id;
    private LocalDate checkDate;
    private LocalDateTime checkTime;
}
