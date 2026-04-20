package com.travelMemory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AiDiaryGenerateRequest {

    @NotBlank(message = "旅行主题不能为空")
    private String title;

    @NotBlank(message = "目的地不能为空")
    private String destination;

    @NotNull(message = "旅行天数不能为空")
    private Integer travelDays;

    @NotBlank(message = "写作风格不能为空")
    private String style;

    private String highlights;

    private String description;
}
