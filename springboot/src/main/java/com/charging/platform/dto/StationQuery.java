package com.charging.platform.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class StationQuery {
    @Min(value = 1, message = "页码必须大于等于1")
    private long pageNum = 1;
    @Min(value = 1, message = "每页条数必须大于等于1")
    @Max(value = 100, message = "每页最多查询100条")
    private long pageSize = 10;
    private String keyword;
    private Long regionId;
    private Integer stationType;
    private Integer connectorType;
    private Integer status;
}
