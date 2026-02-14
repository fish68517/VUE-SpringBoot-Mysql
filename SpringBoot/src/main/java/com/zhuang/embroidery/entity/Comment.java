package com.zhuang.embroidery.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 评论实体类
 */
@Entity
@Table(name = "comment", indexes = {
    @Index(name = "idx_artwork_id", columnList = "artwork_id"),
    @Index(name = "idx_topic_id", columnList = "topic_id"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    /**
     * 评论ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 评论内容
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 用户ID
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * 作品ID（可为空）
     */
    @Column
    private Long artworkId;

    /**
     * 话题ID（可为空）
     */
    @Column
    private Long topicId;

    /**
     * 父评论ID（用于回复）
     */
    @Column
    private Long parentId;

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
    }

    /**
     * 更新前自动设置更新时间
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
