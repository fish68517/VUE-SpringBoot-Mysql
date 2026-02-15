package com.zhuang.embroidery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

/**
 * 操作日志实体类
 */
@Entity
@Table(name = "operation_log", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_action", columnList = "action"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationLog {

    /**
     * 操作日志ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 操作类型：login/view/comment/collect等
     */
    @Column(length = 50)
    private String action;

    /**
     * 目标类型：artwork/knowledge/topic等
     */
    @Column(length = 50)
    private String targetType;

    /**
     * 目标ID
     */
    @Column(name = "target_id")
    private Long targetId;

    /**
     * 操作时间
     */
    @Column(nullable = false, updatable = false,name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 创建前自动设置创建时间
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
