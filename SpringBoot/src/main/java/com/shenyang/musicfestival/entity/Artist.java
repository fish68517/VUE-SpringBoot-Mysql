package com.shenyang.musicfestival.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Artist entity representing music festival artists
 */
@Entity
@Table(name = "artists", indexes = {
    @Index(name = "idx_is_local_band", columnList = "is_local_band")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Artist extends BaseEntity {

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "is_local_band", nullable = false)
    private Boolean isLocalBand = false;

}
