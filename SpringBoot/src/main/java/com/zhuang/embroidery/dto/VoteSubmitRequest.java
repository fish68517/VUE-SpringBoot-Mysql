package com.zhuang.embroidery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 投票提交请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteSubmitRequest {

    /**
     * 投票ID
     */
    private Long voteId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 选择的选项
     */
    private String selectedOption;
}
