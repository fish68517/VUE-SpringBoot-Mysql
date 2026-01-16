package com.postgraduate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * School entity representing a postgraduate school.
 * Contains basic school information including tier, location, and introduction.
 */
@Entity
@Table(name = "schools", indexes = {
        @Index(name = "idx_city", columnList = "city"),
        @Index(name = "idx_tier", columnList = "tier"),
        @Index(name = "idx_deleted", columnList = "deleted")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class School extends BaseEntity {

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 50)
    private String tier;

    @Column(length = 255)
    private String website;

    @Column(columnDefinition = "LONGTEXT")
    private String intro;
}
