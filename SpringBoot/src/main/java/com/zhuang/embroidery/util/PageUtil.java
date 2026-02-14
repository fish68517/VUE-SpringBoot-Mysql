package com.zhuang.embroidery.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页工具类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageUtil {

    /**
     * 当前页码（从 1 开始）
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

    /**
     * 验证并调整分页参数
     */
    public static PageUtil validate(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        if (pageSize > 100) {
            pageSize = 100;
        }
        return PageUtil.builder()
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build();
    }

    /**
     * 计算偏移量（用于 SQL LIMIT）
     */
    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }

    /**
     * 计算总页数
     */
    public void calculateTotalPages() {
        if (total == null || total == 0) {
            this.totalPages = 0;
        } else {
            this.totalPages = (int) Math.ceil((double) total / pageSize);
        }
    }

}
