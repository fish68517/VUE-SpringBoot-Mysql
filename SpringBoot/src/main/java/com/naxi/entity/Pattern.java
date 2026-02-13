package com.naxi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "patterns")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pattern {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PatternCategory category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String culturalBackground;

    @Column(nullable = false)
    private String imageUrl;

    private String downloadUrl;

    @Column(columnDefinition = "TEXT")
    private String applicationScenarios;

    private Integer viewCount = 0;

    private Integer downloadCount = 0;

    private Integer collectionCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum PatternCategory {
        七星纹, 东巴衍生纹, 日月纹, 云纹水纹
    }
}
