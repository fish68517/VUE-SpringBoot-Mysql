package com.powerinspection.dto;

import com.powerinspection.entity.NoticeStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NoticeRequest {
    @NotBlank(message = "公告标题不能为空")
    private String title;

    @NotBlank(message = "公告内容不能为空")
    private String content;

    @NotNull(message = "公告状态不能为空")
    private NoticeStatus status;

    private Boolean pinned = Boolean.FALSE;
}
