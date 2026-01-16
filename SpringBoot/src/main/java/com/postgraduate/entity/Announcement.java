package com.postgraduate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Announcement entity representing system-wide announcements published by administrators.
 * Tracks announcement status, sort order, and publication timestamp.
 */
@Entity
@Table(name = "announcements", indexes = {
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_sort_order", columnList = "sort_order"),
        @Index(name = "idx_deleted", columnList = "deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Announcement extends BaseEntity {

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private AnnouncementStatus status;

    @Column(nullable = false)
    private Integer sortOrder;

    @Column(nullable = true)
    private LocalDateTime publishAt;

    public enum AnnouncementStatus {
        DRAFT, PUBLISHED
    }
}
