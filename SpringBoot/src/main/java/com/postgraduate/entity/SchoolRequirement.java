package com.postgraduate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SchoolRequirement entity representing reexamination requirements for a school/major.
 * Contains requirement content and information about who updated it.
 */
@Entity
@Table(name = "school_requirements", indexes = {
        @Index(name = "idx_school_id", columnList = "school_id"),
        @Index(name = "idx_major_id", columnList = "major_id"),
        @Index(name = "idx_deleted", columnList = "deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolRequirement extends BaseEntity {

    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", insertable = false, updatable = false)
    private School school;

    @Column(name = "major_id")
    private Long majorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "major_id", insertable = false, updatable = false)
    private Major major;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(length = 255)
    private String updatedBy;
}
