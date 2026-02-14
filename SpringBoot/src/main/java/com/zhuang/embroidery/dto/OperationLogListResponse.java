package com.zhuang.embroidery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 操作日志列表响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogListResponse {

    /**
     * 操作日志列表
     */
    private List<OperationLogResponse> logs;

    /**
     * 总数
     */
    private Long total;

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页数量
     */
    private Integer pageSize;

}
