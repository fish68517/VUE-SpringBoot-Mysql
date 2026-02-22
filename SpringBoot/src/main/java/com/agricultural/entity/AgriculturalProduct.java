package com.agricultural.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 农资产品实体类
 */
@Entity
@Table(name = "agricultural_products", indexes = {
    @Index(name = "idx_category", columnList = "category"),
    @Index(name = "idx_merchant_id", columnList = "merchant_id"),
    @Index(name = "idx_stock", columnList = "stock")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgriculturalProduct {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;
    
    @Column(length = 50)
    private String category;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column
    private Integer stock;
    
    @Column(name = "merchant_id")
    private Long merchantId;
    
    @Column(name = "applicable_weather", length = 100)
    private String applicableWeather;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
