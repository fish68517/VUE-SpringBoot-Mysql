package com.health.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.LocalDate;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "height", precision = 5, scale = 2)
    private BigDecimal height;

    @Column(name = "weight", precision = 5, scale = 2)
    private BigDecimal weight;

    @Column(name = "blood_pressure", length = 20)
    private String bloodPressure;

    @Column(name = "heart_rate")
    private Integer heartRate;

    @Column(name = "body_temperature", precision = 4, scale = 2)
    private BigDecimal bodyTemperature;

    @Column(name = "blood_oxygen")
    private Integer bloodOxygen;

    @Column(name = "blood_sugar", precision = 5, scale = 2)
    private BigDecimal bloodSugar;

    @Column(name = "sleep_duration", precision = 4, scale = 2)
    private BigDecimal sleepDuration;

    @Column(name = "diet_record", columnDefinition = "TEXT")
    private String dietRecord;

    @Column(name = "exercise_record", columnDefinition = "TEXT")
    private String exerciseRecord;

    @Column(name = "check_results", columnDefinition = "TEXT")
    private String checkResults; // 新增：检查结果备注

    @Column(name = "data_type", length = 50)
    private String dataType;

    // --- 新增：性别相关数据 (GENDER_SPECIFIC) ---
    @Column(name = "menstrual_cycle")
    private Integer menstrualCycle; // 月经周期

    @Column(name = "menstrual_days")
    private Integer menstrualDays; // 月经天数

    @Column(name = "last_menstrual_date")
    private LocalDate lastMenstrualDate; // 最后月经日期

    @Column(name = "pregnancy_status", length = 20)
    private String pregnancyStatus; // 妊娠状态

    @Column(name = "menstrual_symptoms", columnDefinition = "TEXT")
    private String menstrualSymptoms; // 月经症状

    @Column(name = "prostate_health", columnDefinition = "TEXT")
    private String prostateHealth; // 健康状态

    @Column(name = "sexual_function", length = 20)
    private String sexualFunction; // 性功能状态

    // --- 新增：医师审核数据 ---
    @Column(name = "review_status", length = 20)
    private String reviewStatus; // 审核状态: PENDING, REVIEWED

    @Column(name = "review_feedback", columnDefinition = "TEXT")
    private String reviewFeedback; // 审核反馈意见

// ... 其他代码保持不变

    @Column(name = "feedback_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime feedbackDate; // 审核反馈时间

    @Column(name = "recorded_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime recordedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

    // ... 其他代码保持不变
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (recordedAt == null) {
            recordedAt = LocalDateTime.now();
        }
        if (reviewStatus == null) {
            reviewStatus = "PENDING"; // 默认设置为待审核
        }
    }
}
