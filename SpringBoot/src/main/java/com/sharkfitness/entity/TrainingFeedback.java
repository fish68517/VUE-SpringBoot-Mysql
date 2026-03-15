package com.sharkfitness.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
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

    @Lob
    @Column(name = "content")
    private String content;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "feedback_date", nullable = false)
    private LocalDate feedbackDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "coach_id")
    private Long coachId;

    @Column(name = "feeling")
    private String feeling;

    @Lob
    @Column(name = "image_urls")
    private String imageUrls;

    @Lob
    @Column(name = "video_urls")
    private String videoUrls;

    @Lob
    @Column(name = "document_urls")
    private String documentUrls;

    @Column(name = "coach_reply")
    private String coachReply;

    @Column(name = "reply_at")
    private LocalDate replyAt;
}
