package com.powerinspection.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RecordSubmitRequest {
    @NotBlank(message = "巡检结果不能为空")
    private String result;
    private String statusDescription;
    private String imageName;
    private Boolean needRepair = Boolean.FALSE;
    private String defectLevel;
    private List<RecordItemSubmitRequest> items = new ArrayList<>();
}
