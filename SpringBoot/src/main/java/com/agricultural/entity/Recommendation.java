package com.agricultural.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 推荐实体类
 */
@Entity
@Table(name = "recommendations", indexes = {
    @Index(name = "idx_warning_id", columnList = "warning_id"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recommendation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "warning_id")
    private Long warningId;
    
    @Column(name = "product_id")
    private Long productId;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column
    private Integer priority;
    
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    @Column
    @Enumerated(EnumType.STRING)
    private RecommendationStatus status;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = RecommendationStatus.PENDING;
        }
    }
    
    /**
     * 推荐状态枚举
     */
    public enum RecommendationStatus {
        PENDING,     // 待处理
        ACCEPTED,    // 已接受
        REJECTED     // 已拒绝
    }
}
