package com.zhuang.embroidery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 知识列表响应 DTO（包含分页信息）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeListResponse {

    /**
     * 知识列表
     */
    private List<KnowledgeResponse> items;

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
