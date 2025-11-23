package com.sharkfitness.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * FitnessResource entity representing educational fitness content
 */
@Entity
@Table(name = "fitness_resource")
@Data
public class FitnessResource {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(length = 1000)
    private String description;
    
    @Column(name = "content_type", nullable = false, length = 50)
    private String contentType;  // video, article, document
    
    @Column(name = "file_url", length = 500)
    private String fileUrl;
    
    @Column(columnDefinition = "TEXT")
    private String content;  // For text articles
    
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;
    
    @Column(name = "view_count")
    private Integer viewCount = 0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (viewCount == null) {
            viewCount = 0;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
