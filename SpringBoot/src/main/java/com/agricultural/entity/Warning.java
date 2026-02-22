package com.agricultural.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 预警实体类
 */
@Entity
@Table(name = "warnings", indexes = {
    @Index(name = "idx_region", columnList = "region"),
    @Index(name = "idx_warning_type", columnList = "warning_type"),
    @Index(name = "idx_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warning {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "warning_type", nullable = false, length = 50)
    private String warningType;
    
    @Column(nullable = false, length = 100)
    private String region;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WarningSeverity severity;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column
    @Enumerated(EnumType.STRING)
    private WarningStatus status;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = WarningStatus.ACTIVE;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * 预警等级枚举
     */
    public enum WarningSeverity {
        LOW,         // 低
        MEDIUM,      // 中
        HIGH,        // 高
        CRITICAL     // 严重
    }
    
    /**
     * 预警状态枚举
     */
    public enum WarningStatus {
        ACTIVE,      // 活跃
        EXPIRED,     // 过期
        CANCELLED    // 取消
    }
}
