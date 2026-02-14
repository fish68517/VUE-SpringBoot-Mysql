package com.zhuang.embroidery.controller;

import com.zhuang.embroidery.dto.ApiResponse;
import com.zhuang.embroidery.dto.TopicCreateRequest;
import com.zhuang.embroidery.dto.TopicListResponse;
import com.zhuang.embroidery.dto.TopicResponse;
import com.zhuang.embroidery.dto.TopicUpdateRequest;
import com.zhuang.embroidery.service.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 话题相关 API 控制器
 */
@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
@Slf4j
public class TopicController {

    private final TopicService topicService;

    /**
     * 获取话题列表
     *
     * @param pageNum 页码（默认1）
     * @param pageSize 每页数量（默认10）
     * @return 话题列表响应
     */
    @GetMapping
    public ApiResponse<TopicListResponse> getTopicList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("获取话题列表: pageNum={}, pageSize={}", pageNum, pageSize);

        try {
            TopicListResponse response = topicService.getTopicList(pageNum, pageSize);
            log.info("成功获取话题列表，共 {} 条", response.getTotal());
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("获取话题列表失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("获取话题列表异常", e);
            return ApiResponse.serverError("获取话题列表失败");
        }
    }

    /**
     * 获取话题详情
     *
     * @param id 话题ID
     * @return 话题响应
     */
    @GetMapping("/{id}")
    public ApiResponse<TopicResponse> getTopicDetail(@PathVariable Long id) {
        log.info("获取话题详情: id={}", id);

        try {
            TopicResponse response = topicService.getTopicDetail(id);
            log.info("成功获取话题详情: id={}", id);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("获取话题详情失败: {}", e.getMessage());
            return ApiResponse.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("获取话题详情异常", e);
            return ApiResponse.serverError("获取话题详情失败");
        }
    }

    /**
     * 创建话题（管理员）
     *
     * @param request 话题创建请求
     * @return 话题响应
     */
    @PostMapping
    public ApiResponse<TopicResponse> createTopic(@RequestBody TopicCreateRequest request) {
        log.info("创建话题: title={}, createdBy={}", request.getTitle(), request.getCreatedBy());

        try {
            TopicResponse response = topicService.createTopic(request);
            log.info("话题创建成功: id={}", response.getId());
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("创建话题失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("创建话题异常", e);
            return ApiResponse.serverError("创建话题失败");
        }
    }

    /**
     * 编辑话题（管理员）
     *
     * @param id 话题ID
     * @param request 话题更新请求
     * @return 话题响应
     */
    @PutMapping("/{id}")
    public ApiResponse<TopicResponse> updateTopic(
            @PathVariable Long id,
            @RequestBody TopicUpdateRequest request) {
        log.info("编辑话题: id={}", id);

        try {
            TopicResponse response = topicService.updateTopic(id, request);
            log.info("话题编辑成功: id={}", id);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("编辑话题失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("编辑话题异常", e);
            return ApiResponse.serverError("编辑话题失败");
        }
    }

    /**
     * 删除话题（管理员）
     *
     * @param id 话题ID
     * @return 成功响应
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTopic(@PathVariable Long id) {
        log.info("删除话题: id={}", id);

        try {
            topicService.deleteTopic(id);
            log.info("话题删除成功: id={}", id);
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            log.warn("删除话题失败: {}", e.getMessage());
            return ApiResponse.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("删除话题异常", e);
            return ApiResponse.serverError("删除话题失败");
        }
    }

    /**
     * 置顶话题（管理员）
     *
     * @param id 话题ID
     * @return 话题响应
     */
    @PostMapping("/{id}/pin")
    public ApiResponse<TopicResponse> pinTopic(@PathVariable Long id) {
        log.info("置顶话题: id={}", id);

        try {
            TopicResponse response = topicService.pinTopic(id);
            log.info("话题置顶成功: id={}", id);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("置顶话题失败: {}", e.getMessage());
            return ApiResponse.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("置顶话题异常", e);
            return ApiResponse.serverError("置顶话题失败");
        }
    }

    /**
     * 取消置顶话题（管理员）
     *
     * @param id 话题ID
     * @return 话题响应
     */
    @PostMapping("/{id}/unpin")
    public ApiResponse<TopicResponse> unpinTopic(@PathVariable Long id) {
        log.info("取消置顶话题: id={}", id);

        try {
            TopicResponse response = topicService.unpinTopic(id);
            log.info("话题取消置顶成功: id={}", id);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("取消置顶话题失败: {}", e.getMessage());
            return ApiResponse.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("取消置顶话题异常", e);
            return ApiResponse.serverError("取消置顶话题失败");
        }
    }

}
