package com.zhuang.embroidery.controller;

import com.zhuang.embroidery.dto.ApiResponse;
import com.zhuang.embroidery.dto.VoteCreateRequest;
import com.zhuang.embroidery.dto.VoteListResponse;
import com.zhuang.embroidery.dto.VoteResponse;
import com.zhuang.embroidery.dto.VoteStatisticsResponse;
import com.zhuang.embroidery.dto.VoteSubmitRequest;
import com.zhuang.embroidery.service.VoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 投票相关 API 控制器
 */
@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
@Slf4j
public class VoteController {

    private final VoteService voteService;

    /**
     * 获取投票列表
     *
     * @param pageNum 页码（默认1）
     * @param pageSize 每页数量（默认10）
     * @return 投票列表响应
     */
    @GetMapping
    public ApiResponse<VoteListResponse> getVoteList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("获取投票列表: pageNum={}, pageSize={}", pageNum, pageSize);

        try {
            VoteListResponse response = voteService.getVoteList(pageNum, pageSize);
            log.info("成功获取投票列表，共 {} 条", response.getTotal());
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("获取投票列表失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("获取投票列表异常", e);
            return ApiResponse.serverError("获取投票列表失败");
        }
    }

    /**
     * 获取投票详情
     *
     * @param id 投票ID
     * @return 投票响应
     */
    @GetMapping("/{id}")
    public ApiResponse<VoteResponse> getVoteDetail(@PathVariable Long id) {
        log.info("获取投票详情: id={}", id);

        try {
            VoteResponse response = voteService.getVoteDetail(id);
            log.info("成功获取投票详情: id={}", id);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("获取投票详情失败: {}", e.getMessage());
            return ApiResponse.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("获取投票详情异常", e);
            return ApiResponse.serverError("获取投票详情失败");
        }
    }

    /**
     * 提交投票
     *
     * @param request 投票提交请求
     * @return 成功响应
     */
    @PostMapping("/submit")
    public ApiResponse<Void> submitVote(@RequestBody VoteSubmitRequest request) {
        log.info("提交投票: voteId={}, userId={}", request.getVoteId(), request.getUserId());

        try {
            voteService.submitVote(request);
            log.info("投票提交成功: voteId={}, userId={}", request.getVoteId(), request.getUserId());
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            log.warn("提交投票失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("提交投票异常", e);
            return ApiResponse.serverError("提交投票失败");
        }
    }

    /**
     * 获取投票统计
     *
     * @param id 投票ID
     * @return 投票统计响应
     */
    @GetMapping("/{id}/statistics")
    public ApiResponse<VoteStatisticsResponse> getVoteStatistics(@PathVariable Long id) {
        log.info("获取投票统计: id={}", id);

        try {
            VoteStatisticsResponse response = voteService.getVoteStatistics(id);
            log.info("成功获取投票统计: id={}", id);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("获取投票统计失败: {}", e.getMessage());
            return ApiResponse.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("获取投票统计异常", e);
            return ApiResponse.serverError("获取投票统计失败");
        }
    }
}
