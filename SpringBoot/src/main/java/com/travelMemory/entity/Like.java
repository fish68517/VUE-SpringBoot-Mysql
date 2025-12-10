package com.travelMemory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "likes", indexes = {
        @Index(name = "idx_travel_record_id", columnList = "travel_record_id"),
        @Index(name = "idx_user_id", columnList = "user_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "unique_like", columnNames = {"travel_record_id", "user_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 修改点 1：显式指定数据库列名为 travel_record_id
    @Column(name = "travel_record_id", nullable = false)
    private Long travelRecordId;

    // 修改点 2：显式指定数据库列名为 user_id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 修改点 3：建议也显式指定 created_at，保持风格一致且安全
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}