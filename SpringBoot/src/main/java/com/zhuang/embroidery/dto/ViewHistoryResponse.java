package com.zhuang.embroidery.dto;

import com.zhuang.embroidery.entity.ViewHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 浏览历史响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewHistoryResponse {

    /**
     * 浏览历史ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 内容类型：artwork/knowledge
     */
    private String contentType;

    /**
     * 内容ID
     */
    private Long contentId;

    /**
     * 浏览时间
     */
    private LocalDateTime viewedAt;

    /**
     * 从 ViewHistory 实体转换为 ViewHistoryResponse
     */
    public static ViewHistoryResponse fromViewHistory(ViewHistory viewHistory) {
        return ViewHistoryResponse.builder()
                .id(viewHistory.getId())
                .userId(viewHistory.getUserId())
                .contentType(viewHistory.getContentType())
                .contentId(viewHistory.getContentId())
                .viewedAt(viewHistory.getViewedAt())
                .build();
    }

}
