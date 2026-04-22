package com.powerinspection.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RepairOrderCreateRequest {
    @NotNull(message = "维护员不能为空")
    private Long maintainerId;
    private String remark;
}
