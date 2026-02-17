package com.naxi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "creative_works")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreativeWork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1. 设置为只读副本：通过映射 user_id 列，但禁止 JPA 通过此字段写入
    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    // 2. 负责实际写入：这个关联对象负责维护数据库中的 user_id 值
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkStatus status = WorkStatus.PENDING;

    private Integer likeCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum WorkStatus {
        PENDING, APPROVED, REJECTED
    }
}
