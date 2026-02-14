package com.zhuang.embroidery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 投票列表响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteListResponse {

    /**
     * 投票列表
     */
    private List<VoteResponse> items;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer totalPages;
}
