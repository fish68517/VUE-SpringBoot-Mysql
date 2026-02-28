package com.health.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 健康建议实体类
 * 代表医师为患者提供的个性化健康建议
 */
@Entity
@Table(name = "health_advice")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthAdvice {

    /**
     * 健康建议ID - 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 医师ID - 外键
     */
    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    /**
     * 患者ID - 外键
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 建议内容
     */
    @Column(name = "advice_content", nullable = false, columnDefinition = "TEXT")
    private String adviceContent;

    /**
     * 推荐信息
     */
    @Column(name = "recommendation", columnDefinition = "TEXT")
    private String recommendation;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 在保存前设置创建时间和更新时间
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * 在更新前设置更新时间
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
