package com.powerinspection.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeviceCategoryRequest {
    @NotBlank(message = "分类编码不能为空")
    private String code;
    @NotBlank(message = "分类名称不能为空")
    private String name;
    private String description;
}
