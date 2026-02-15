package com.zhuang.embroidery.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 投票实体类
 */
@Entity
@Table(name = "vote", indexes = {
    @Index(name = "idx_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {

    /**
     * 投票ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 投票标题
     */
    @Column(nullable = false, length = 255)
    private String title;

    /**
     * 投票描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 投票选项列表（JSON格式）
     */
    @Column(columnDefinition = "JSON")
    private String options;

    /**
     * 状态：active/closed
     */
    @Column(length = 20)
    private String status;

    /**
     * 创建时间
     */
    @Column(nullable = false, updatable = false,name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 结束时间
     */
    @Column
    private LocalDateTime endAt;

    /**
     * 创建前自动设置创建时间
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = "active";
        }
    }
}
