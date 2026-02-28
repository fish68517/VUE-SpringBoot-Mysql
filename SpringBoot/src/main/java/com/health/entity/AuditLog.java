package com.health.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 审计日志实体类
 * 记录系统中所有重要操作
 */
@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    /**
     * 审计日志ID - 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID - 外键
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 操作类型
     */
    @Column(name = "action", length = 100)
    private String action;

    /**
     * 操作的资源
     */
    @Column(name = "resource", length = 100)
    private String resource;

    /**
     * 操作时间戳
     */
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    /**
     * 操作详情
     */
    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    /**
     * 在保存前设置时间戳
     */
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
