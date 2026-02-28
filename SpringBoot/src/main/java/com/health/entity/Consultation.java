package com.health.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 咨询实体类
 * 代表用户与医师之间的咨询记录
 */
@Entity
@Table(name = "consultations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consultation {

    /**
     * 咨询ID - 主键
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
     * 医师ID - 外键
     */
    @Column(name = "doctor_id")
    private Long doctorId;

    /**
     * 咨询问题
     */
    @Column(name = "question", nullable = false, columnDefinition = "TEXT")
    private String question;

    /**
     * 医师回答
     */
    @Column(name = "answer", columnDefinition = "TEXT")
    private String answer;

    /**
     * 咨询状态 - PENDING(待回答), ANSWERED(已回答)
     */
    @Column(name = "status", length = 20)
    private String status;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 回答时间
     */
    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    /**
     * 在保存前设置创建时间和状态
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = "PENDING";
        }
    }
}
