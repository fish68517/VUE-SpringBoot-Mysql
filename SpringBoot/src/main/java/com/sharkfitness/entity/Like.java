package com.sharkfitness.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Like entity representing user likes on dynamic posts
 */
@Entity
@Table(name = "like_record",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "dynamic_id"}))
@Data
public class Like {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "dynamic_id", nullable = false)
    private Dynamic dynamic;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
