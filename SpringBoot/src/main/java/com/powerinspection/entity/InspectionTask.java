package com.powerinspection.entity;

import com.powerinspection.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "inspection_task")
public class InspectionTask extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_no", nullable = false, unique = true, length = 50)
    private String taskNo;

    @Column(nullable = false, length = 100)
    private String title;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "inspector_id", nullable = false)
    private User inspector;

    @Column(name = "planned_date", nullable = false)
    private LocalDate plannedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TaskStatus status = TaskStatus.PENDING;

    @Column(nullable = false, length = 20)
    private String priority;

    @Column(length = 255)
    private String remark;
}
