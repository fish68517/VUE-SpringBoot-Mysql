package com.tourism.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 景点标签实体类
 */
@Entity
@Table(name = "attraction_tags", indexes = {
    @Index(name = "idx_attraction_id", columnList = "attraction_id"),
    @Index(name = "idx_tag_name", columnList = "tag_name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttractionTag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long attractionId;
    
    @Column(nullable = false, length = 50)
    private String tagName;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
