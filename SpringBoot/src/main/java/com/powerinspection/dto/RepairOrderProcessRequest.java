package com.powerinspection.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RepairOrderProcessRequest {
    @NotBlank(message = "处理措施不能为空")
    private String measures;
    @NotBlank(message = "处理结果不能为空")
    private String result;
    private String imageName;
    private String remark;
}
