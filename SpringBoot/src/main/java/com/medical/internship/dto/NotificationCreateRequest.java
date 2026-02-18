package com.medical.internship.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建通知请求DTO (管理员专用)
 */
@Data
public class NotificationCreateRequest {

    @NotNull(message = "接收用户ID不能为空")
    private Long userId;

    @NotBlank(message = "通知类型不能为空")
    private String type;

    @NotBlank(message = "通知内容不能为空")
    private String content;
}