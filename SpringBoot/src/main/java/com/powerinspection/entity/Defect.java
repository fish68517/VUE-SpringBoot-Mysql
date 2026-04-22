package com.powerinspection.entity;

import com.powerinspection.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "defect")
public class Defect extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "record_id", nullable = false, unique = true)
    private InspectionRecord record;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Column(nullable = false, length = 20)
    private String level;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DefectStatus status = DefectStatus.REPORTED;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "image_name", length = 255)
    private String imageName;

    @Column(name = "reported_at", nullable = false)
    private LocalDateTime reportedAt;
}
