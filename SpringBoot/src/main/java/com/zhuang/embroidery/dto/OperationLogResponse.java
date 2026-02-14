package com.zhuang.embroidery.dto;

import com.zhuang.embroidery.entity.OperationLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 操作日志响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogResponse {

    /**
     * 操作日志ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 操作类型
     */
    private String action;

    /**
     * 目标类型
     */
    private String targetType;

    /**
     * 目标ID
     */
    private Long targetId;

    /**
     * 操作时间
     */
    private LocalDateTime createdAt;

    /**
     * 从 OperationLog 实体转换为 OperationLogResponse
     */
    public static OperationLogResponse fromOperationLog(OperationLog log) {
        return OperationLogResponse.builder()
                .id(log.getId())
                .userId(log.getUserId())
                .action(log.getAction())
                .targetType(log.getTargetType())
                .targetId(log.getTargetId())
                .createdAt(log.getCreatedAt())
                .build();
    }

}
