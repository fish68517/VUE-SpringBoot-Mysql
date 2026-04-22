package com.powerinspection.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskRequest {
    @NotBlank(message = "任务标题不能为空")
    private String title;
    @NotNull(message = "设备不能为空")
    private Long deviceId;
    @NotNull(message = "巡检员不能为空")
    private Long inspectorId;
    @NotNull(message = "计划时间不能为空")
    private LocalDate plannedDate;
    @NotBlank(message = "优先级不能为空")
    private String priority;
    private String remark;
}
