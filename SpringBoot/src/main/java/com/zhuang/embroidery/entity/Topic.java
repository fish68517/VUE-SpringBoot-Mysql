package com.zhuang.embroidery.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 话题实体类
 */
@Entity
@Table(name = "topic", indexes = {
    @Index(name = "idx_is_pinned", columnList = "is_pinned"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Topic {

    /**
     * 话题ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 话题标题
     */
    @Column(nullable = false, length = 255)
    private String title;

    /**
     * 话题描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 是否置顶
     */
    @Column(nullable = false)
    private Boolean isPinned;

    /**
     * 创建者ID
     */
    @Column(nullable = false)
    private Long createdBy;

    /**
     * 创建时间
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 创建前自动设置创建时间和更新时间
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isPinned == null) {
            isPinned = false;
        }
    }

    /**
     * 更新前自动设置更新时间
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
