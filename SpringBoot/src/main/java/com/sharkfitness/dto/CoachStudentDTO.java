package com.sharkfitness.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CoachStudentDTO {
    private Long id;
    private Long coachId;
    private Long studentId;
    private String createdAt;
    // 修改这里：类型改为 LocalDateTime，并指定 pattern 处理 'Z'
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    private LocalDateTime expireAt;
    private int status;
}
