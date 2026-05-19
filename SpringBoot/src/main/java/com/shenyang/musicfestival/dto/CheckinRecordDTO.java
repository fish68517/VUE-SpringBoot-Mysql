package com.shenyang.musicfestival.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CheckinRecordDTO {
    private Long id;
    private Long taskId;
    private String taskName;
    private Integer points;
    private String photo;
    private String description;
    private String status;
    private String rejectReason;
    private LocalDateTime createdAt;
}
