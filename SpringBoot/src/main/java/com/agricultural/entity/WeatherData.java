package com.agricultural.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 气象数据实体类
 */
@Entity
@Table(name = "weather_data", indexes = {
    @Index(name = "idx_region", columnList = "region"),
    @Index(name = "idx_recorded_at", columnList = "recorded_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String region;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal temperature;
    
    @Column
    private Integer humidity;
    
    @Column(precision = 8, scale = 2)
    private BigDecimal precipitation;
    
    @Column(name = "wind_speed", precision = 5, scale = 2)
    private BigDecimal windSpeed;
    
    @Column(name = "weather_condition", length = 50)
    private String weatherCondition;
    
    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (recordedAt == null) {
            recordedAt = LocalDateTime.now();
        }
    }
}
