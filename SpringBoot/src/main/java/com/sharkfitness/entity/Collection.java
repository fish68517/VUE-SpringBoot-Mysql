package com.sharkfitness.entity;

import lombok.Data;
 import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Collection entity representing user's saved fitness resources
 */
@Entity
@Table(name = "collection",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "resource_id"}))
@Data
public class Collection {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "resource_id", nullable = false)
    private FitnessResource resource;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
