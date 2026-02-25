package com.tourism.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 景点实体类
 */
@Entity
@Table(name = "attractions", indexes = {
    @Index(name = "idx_name", columnList = "name"),
    @Index(name = "idx_guangzhou_special", columnList = "is_guangzhou_special")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attraction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(columnDefinition = "LONGTEXT")
    private String description;
    
    @Column(length = 200)
    private String location;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal ticketPrice;
    
    @Column(length = 100)
    private String openingHours;
    
    @Column(length = 500)
    private String imageUrl;
    
    @Column(nullable = false)
    private Boolean isGuangzhouSpecial;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isGuangzhouSpecial == null) {
            isGuangzhouSpecial = false;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
