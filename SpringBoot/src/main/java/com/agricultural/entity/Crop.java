package com.agricultural.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 作物实体类
 */
@Entity
@Table(name = "crops", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_region", columnList = "region")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Crop {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "crop_name", nullable = false, length = 50)
    private String cropName;
    
    @Column(name = "growth_stage", length = 50)
    private String growthStage;
    
    @Column(length = 100)
    private String region;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "planting_date")
    private LocalDate plantingDate;
    
    @Column(name = "expected_harvest_date")
    private LocalDate expectedHarvestDate;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
