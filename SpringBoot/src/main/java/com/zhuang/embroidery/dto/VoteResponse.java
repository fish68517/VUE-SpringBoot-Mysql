package com.zhuang.embroidery.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhuang.embroidery.entity.Vote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 投票响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteResponse {

    /**
     * 投票ID
     */
    private Long id;

    /**
     * 投票标题
     */
    private String title;

    /**
     * 投票描述
     */
    private String description;

    /**
     * 投票选项列表
     */
    private List<String> options;

    /**
     * 状态：active/closed
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 结束时间
     */
    private LocalDateTime endAt;

    /**
     * 从 Vote 实体转换为 VoteResponse
     */
    public static VoteResponse fromVote(Vote vote) {
        List<String> options = null;
        if (vote.getOptions() != null && !vote.getOptions().isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                options = mapper.readValue(vote.getOptions(), List.class);
            } catch (Exception e) {
                options = List.of();
            }
        }

        return VoteResponse.builder()
                .id(vote.getId())
                .title(vote.getTitle())
                .description(vote.getDescription())
                .options(options)
                .status(vote.getStatus())
                .createdAt(vote.getCreatedAt())
                .endAt(vote.getEndAt())
                .build();
    }
}
