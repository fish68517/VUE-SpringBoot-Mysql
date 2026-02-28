package com.health.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 医师实体类
 * 代表系统中的医师信息
 */
@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {

    /**
     * 医师ID - 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 关联的用户ID - 外键
     */
    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    /**
     * 执业证号 - 唯一
     */
    @Column(name = "license_number", unique = true, nullable = false, length = 50)
    private String licenseNumber;

    /**
     * 专科
     */
    @Column(name = "specialization", length = 100)
    private String specialization;

    /**
     * 医院
     */
    @Column(name = "hospital", length = 100)
    private String hospital;

    /**
     * 是否认证
     */
    @Column(name = "verified")
    private Boolean verified;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 在保存前设置创建时间
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (verified == null) {
            verified = false;
        }
    }
}
