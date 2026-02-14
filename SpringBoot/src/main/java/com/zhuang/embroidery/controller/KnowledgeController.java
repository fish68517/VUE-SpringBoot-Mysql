package com.zhuang.embroidery.controller;

import com.zhuang.embroidery.dto.ApiResponse;
import com.zhuang.embroidery.dto.KnowledgeListResponse;
import com.zhuang.embroidery.dto.KnowledgeResponse;
import com.zhuang.embroidery.service.KnowledgeService;
import com.zhuang.embroidery.service.ViewHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识相关 API 控制器
 */
@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
@Slf4j
public class KnowledgeController {

    private final KnowledgeService knowledgeService;
    private final ViewHistoryService viewHistoryService;

    /**
     * 获取知识文章列表（支持分类、分页）
     *
     * @param category 分类（可选）
     * @param pageNum 页码（默认1）
     * @param pageSize 每页数量（默认10）
     * @return 知识文章列表响应
     */
    @GetMapping
    public ApiResponse<KnowledgeListResponse> getKnowledgeList(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("获取知识文章列表: category={}, pageNum={}, pageSize={}", category, pageNum, pageSize);

        try {
            KnowledgeListResponse response = knowledgeService.getKnowledgeList(category, pageNum, pageSize);
            log.info("成功获取知识文章列表，共 {} 条", response.getTotal());
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("获取知识文章列表失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("获取知识文章列表异常", e);
            return ApiResponse.serverError("获取知识文章列表失败");
        }
    }

    /**
     * 获取知识文章详情
     *
     * @param id 知识ID
     * @return 知识文章详情响应
     */
    @GetMapping("/{id}")
    public ApiResponse<KnowledgeResponse> getKnowledgeDetail(@PathVariable Long id) {
        log.info("获取知识文章详情: id={}", id);

        try {
            KnowledgeResponse response = knowledgeService.getKnowledgeDetail(id);
            log.info("成功获取知识文章详情: id={}", id);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("获取知识文章详情失败: {}", e.getMessage());
            return ApiResponse.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("获取知识文章详情异常", e);
            return ApiResponse.serverError("获取知识文章详情失败");
        }
    }

    /**
     * 获取知识文章分类列表
     *
     * @return 分类列表响应
     */
    @GetMapping("/categories/all")
    public ApiResponse<List<String>> getCategories() {
        log.info("获取知识文章分类列表");

        try {
            List<String> categories = knowledgeService.getAllCategories();
            log.info("成功获取知识文章分类列表，共 {} 个分类", categories.size());
            return ApiResponse.success(categories);
        } catch (Exception e) {
            log.error("获取知识文章分类列表异常", e);
            return ApiResponse.serverError("获取知识文章分类列表失败");
        }
    }

    /**
     * 记录知识文章浏览
     *
     * @param id 知识ID
     * @param userId 用户ID（可选，用于记录浏览历史）
     * @return 成功响应
     */
    @PostMapping("/{id}/view")
    public ApiResponse<Void> recordKnowledgeView(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId) {
        log.info("记录知识文章浏览: id={}, userId={}", id, userId);

        try {
            // 增加知识文章浏览计数
            knowledgeService.recordKnowledgeView(id);

            // 如果提供了用户ID，记录浏览历史
            if (userId != null && userId > 0) {
                viewHistoryService.recordViewHistory(userId, "knowledge", id);
            }

            log.info("知识文章浏览记录成功: id={}", id);
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            log.warn("记录知识文章浏览失败: {}", e.getMessage());
            return ApiResponse.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("记录知识文章浏览异常", e);
            return ApiResponse.serverError("记录知识文章浏览失败");
        }
    }

}
