package com.agricultural.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WarningUpdateRequest {

    private String description;

    // 增加 @JsonFormat 注解，指定前端传过来的时间格式
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;
}