package com.sharkfitness.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data // Lombok annotation for getters, setters, toString, etc.
@Entity
@Table(name = "training_feedback")
public class TrainingFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Lob // Use @Lob for text type to allow for long feedback content
    @Column(name = "content")
    private String content;

    // Use Integer for rating as it can be null
    @Column(name = "rating")
    private Integer rating;

    @Column(name = "feedback_date", nullable = false)
    private LocalDate feedbackDate;

    @CreationTimestamp // Automatically set the creation timestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // We don't need to map the foreign key constraints directly in the entity
    // for this simple use case, but you could use @ManyToOne if you map
    // TrainingPlan and User entities.
}
