package com.zhuang.embroidery.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 收藏实体类
 */
@Entity
@Table(name = "collection", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_artwork_id", columnList = "artwork_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "unique_user_artwork", columnNames = {"user_id", "artwork_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Collection {

    /**
     * 收藏ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @Column(name="user_id", nullable=false)
    private Long userId;

    /**
     * 作品ID
     */
    @Column(name="artwork_id", nullable=false)
    private Long artworkId;

    /**
     * 收藏时间
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime collectedAt;

    /**
     * 创建前自动设置收藏时间
     */
    @PrePersist
    protected void onCreate() {
        collectedAt = LocalDateTime.now();
    }
}
