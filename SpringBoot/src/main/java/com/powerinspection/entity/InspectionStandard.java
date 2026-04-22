package com.powerinspection.entity;

import com.powerinspection.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "inspection_standard")
public class InspectionStandard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private DeviceCategory category;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(name = "standard_value", length = 255)
    private String standardValue;

    @Column(name = "check_method", length = 255)
    private String checkMethod;

    @Column(name = "cycle_days")
    private Integer cycleDays;

    @Column(name = "abnormal_rule", length = 255)
    private String abnormalRule;

    @Column(length = 255)
    private String remark;
}
