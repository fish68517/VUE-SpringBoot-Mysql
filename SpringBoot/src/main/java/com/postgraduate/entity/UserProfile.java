package com.postgraduate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * UserProfile entity representing a user's academic profile information.
 * Contains educational background, test scores, and target information.
 */
@Entity
@Table(name = "user_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "undergrad_tier", length = 50)
    private String undergradTier;

    @Column(name = "gpa", precision = 5, scale = 2)
    private BigDecimal gpa;

    @Column(name = "gpa_scale", length = 50)
    private String gpaScale;

    @Column(name = "cet4_score")
    private Integer cet4Score;

    @Column(name = "cet6_score")
    private Integer cet6Score;

    @Column(name = "target_score")
    private Integer targetScore;

    @Column(name = "other_scores", columnDefinition = "TEXT")
    private String otherScores;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
