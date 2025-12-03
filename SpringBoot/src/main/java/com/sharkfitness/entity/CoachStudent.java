package com.sharkfitness.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * CoachStudent entity representing the relationship between coaches and their students
 */
@Entity
@Table(name = "coach_student",
       uniqueConstraints = @UniqueConstraint(columnNames = {"coach_id", "student_id"}))
@Data
public class CoachStudent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "coach_id", nullable = false)
    private User coach;
    
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = true)
    private LocalDateTime createdAt;
}
