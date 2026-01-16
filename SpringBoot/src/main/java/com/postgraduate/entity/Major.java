package com.postgraduate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Major entity representing a major/program offered by a school.
 * Contains major name and direction information.
 */
@Entity
@Table(name = "majors", indexes = {
        @Index(name = "idx_school_id", columnList = "school_id"),
        @Index(name = "idx_deleted", columnList = "deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Major extends BaseEntity {

    @Column(name = "school_id", nullable = false)
    private Long schoolId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", insertable = false, updatable = false)
    private School school;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 255)
    private String direction;
}
