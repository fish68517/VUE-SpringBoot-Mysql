package com.health.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 健康数据实体类
 * 代表用户记录的健康数据
 */
@Entity
@Table(name = "health_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthData {

    /**
     * 健康数据ID - 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID - 外键
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 身高（单位：cm）
     */
    @Column(name = "height", precision = 5, scale = 2)
    private BigDecimal height;

    /**
     * 体重（单位：kg）
     */
    @Column(name = "weight", precision = 5, scale = 2)
    private BigDecimal weight;

    /**
     * 血压
     */
    @Column(name = "blood_pressure", length = 20)
    private String bloodPressure;

    /**
     * 心率（单位：次/分钟）
     */
    @Column(name = "heart_rate")
    private Integer heartRate;

    /**
     * 饮食记录
     */
    @Column(name = "diet_record", columnDefinition = "TEXT")
    private String dietRecord;

    /**
     * 运动记录
     */
    @Column(name = "exercise_record", columnDefinition = "TEXT")
    private String exerciseRecord;

    /**
     * 数据类型 - ROUTINE(常规), GENDER_SPECIFIC(性别相关)
     */
    @Column(name = "data_type", length = 50)
    private String dataType;

    /**
     * 记录时间
     */
    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 在保存前设置创建时间和记录时间
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (recordedAt == null) {
            recordedAt = LocalDateTime.now();
        }
    }
}
