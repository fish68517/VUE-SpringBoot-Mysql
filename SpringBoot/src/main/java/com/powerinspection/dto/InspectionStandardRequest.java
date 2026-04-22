package com.powerinspection.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InspectionStandardRequest {
    @NotNull(message = "设备分类不能为空")
    private Long categoryId;

    @NotBlank(message = "巡检项目不能为空")
    private String itemName;

    private String standardValue;
    private String checkMethod;
    private Integer cycleDays;
    private String abnormalRule;
    private String remark;
}
