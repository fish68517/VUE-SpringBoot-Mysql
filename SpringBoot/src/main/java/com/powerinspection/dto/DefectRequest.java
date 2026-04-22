package com.powerinspection.dto;

import com.powerinspection.entity.DefectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DefectRequest {

    @NotNull(message = "巡检记录不能为空")
    private Long recordId;

    @NotBlank(message = "缺陷等级不能为空")
    private String level;

    @NotNull(message = "缺陷状态不能为空")
    private DefectStatus status;

    @NotBlank(message = "缺陷描述不能为空")
    private String description;

    private String imageName;
}
