package com.zhuang.embroidery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 反馈列表响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackListResponse {

    /**
     * 反馈列表
     */
    private List<FeedbackResponse> items;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 总数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer totalPages;

}
