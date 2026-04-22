package com.powerinspection.entity;

import com.powerinspection.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "inspection_record")
public class InspectionRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "task_id", nullable = false, unique = true)
    private InspectionTask task;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "inspector_id", nullable = false)
    private User inspector;

    @Column(nullable = false, length = 20)
    private String result;

    @Column(name = "status_description", length = 500)
    private String statusDescription;

    @Column(name = "image_name", length = 255)
    private String imageName;

    @Column(name = "inspection_time", nullable = false)
    private LocalDateTime inspectionTime;

    @Column(name = "need_repair", nullable = false)
    private Boolean needRepair = Boolean.FALSE;
}
