package com.powerinspection.entity;

import com.powerinspection.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "device")
public class Device extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private DeviceCategory category;

    @Column(name = "device_code", nullable = false, unique = true, length = 50)
    private String deviceCode;

    @Column(name = "device_name", nullable = false, length = 100)
    private String deviceName;

    @Column(nullable = false, length = 100)
    private String location;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "last_inspection_time")
    private LocalDateTime lastInspectionTime;

    @Column(name = "image_name", length = 255)
    private String imageName;

    @Column(length = 255)
    private String remark;
}
