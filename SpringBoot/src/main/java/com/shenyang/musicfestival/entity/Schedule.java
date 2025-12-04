package com.shenyang.musicfestival.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Schedule entity representing festival performance schedule
 */
@Entity
@Table(name = "schedules", indexes = {
    @Index(name = "idx_festival_id", columnList = "festival_id"),
    @Index(name = "idx_artist_id", columnList = "artist_id"),
    @Index(name = "idx_start_time", columnList = "start_time")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule extends BaseEntity {

    @Column(name = "festival_id", nullable = false)
    private Long festivalId;

    @Column(name = "artist_id", nullable = false)
    private Long artistId;

    @Column(name = "stage_name", length = 100)
    private String stageName;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "festival_id", insertable = false, updatable = false)
    private Festival festival;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", insertable = false, updatable = false)
    private Artist artist;

}
