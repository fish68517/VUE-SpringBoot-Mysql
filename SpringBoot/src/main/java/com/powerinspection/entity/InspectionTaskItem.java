package com.powerinspection.entity;

import com.powerinspection.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "inspection_task_item")
public class InspectionTaskItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "task_id", nullable = false)
    private InspectionTask task;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "standard_id")
    private InspectionStandard standard;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(name = "standard_value", length = 255)
    private String standardValue;

    @Column(name = "check_method", length = 255)
    private String checkMethod;

    @Column(name = "abnormal_rule", length = 255)
    private String abnormalRule;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "checked_value", length = 255)
    private String checkedValue;

    @Column(name = "item_result", length = 20)
    private String itemResult;

    @Column(name = "item_remark", length = 255)
    private String itemRemark;
}
