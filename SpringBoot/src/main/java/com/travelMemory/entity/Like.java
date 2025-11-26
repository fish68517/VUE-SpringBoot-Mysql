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

    @Column(nullable = false)
    private Long travelRecordId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
