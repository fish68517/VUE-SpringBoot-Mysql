package com.zhuang.embroidery.controller;

import com.zhuang.embroidery.dto.ApiResponse;
import com.zhuang.embroidery.dto.ArtworkListResponse;
import com.zhuang.embroidery.dto.ArtworkResponse;
import com.zhuang.embroidery.service.ArtworkService;
import com.zhuang.embroidery.service.CollectionService;
import com.zhuang.embroidery.service.ViewHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 作品相关 API 控制器
 */
@RestController
@RequestMapping("/api/artworks")
@RequiredArgsConstructor
@Slf4j
public class ArtworkController {

    private final ArtworkService artworkService;
    private final CollectionService collectionService;
    private final ViewHistoryService viewHistoryService;

    /**
     * 获取作品列表（支持分类、分页）
     *
     * @param category 分类（可选）
     * @param pageNum 页码（默认1）
     * @param pageSize 每页数量（默认10）
     * @return 作品列表响应
     */
    @GetMapping
    public ApiResponse<ArtworkListResponse> getArtworkList(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("获取作品列表: category={}, pageNum={}, pageSize={}", category, pageNum, pageSize);

        try {
            ArtworkListResponse response = artworkService.getArtworkList(category, pageNum, pageSize);
            log.info("成功获取作品列表，共 {} 条", response.getTotal());
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("获取作品列表失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("获取作品列表异常", e);
            return ApiResponse.serverError("获取作品列表失败");
        }
    }

    /**
     * 获取作品详情
     *
     * @param id 作品ID
     * @return 作品详情响应
     */
    @GetMapping("/{id}")
    public ApiResponse<ArtworkResponse> getArtworkDetail(@PathVariable Long id) {
        log.info("获取作品详情: id={}", id);

        try {
            ArtworkResponse response = artworkService.getArtworkDetail(id);
            log.info("成功获取作品详情: id={}", id);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("获取作品详情失败: {}", e.getMessage());
            return ApiResponse.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("获取作品详情异常", e);
            return ApiResponse.serverError("获取作品详情失败");
        }
    }

    /**
     * 获取作品分类列表
     *
     * @return 分类列表响应
     */
    @GetMapping("/categories/all")
    public ApiResponse<List<String>> getCategories() {
        log.info("获取作品分类列表");

        try {
            List<String> categories = artworkService.getAllCategories();
            log.info("成功获取作品分类列表，共 {} 个分类", categories.size());
            return ApiResponse.success(categories);
        } catch (Exception e) {
            log.error("获取作品分类列表异常", e);
            return ApiResponse.serverError("获取作品分类列表失败");
        }
    }

    /**
     * 记录作品浏览
     *
     * @param id 作品ID
     * @param userId 用户ID（可选，用于记录浏览历史）
     * @return 成功响应
     */
    @PostMapping("/{id}/view")
    public ApiResponse<Void> recordArtworkView(
            @PathVariable Long id,
            @RequestParam(required = false) Long userId) {
        log.info("记录作品浏览: id={}, userId={}", id, userId);

        try {
            // 增加作品浏览计数
            artworkService.recordArtworkView(id);

            // 如果提供了用户ID，记录浏览历史
            if (userId != null && userId > 0) {
                viewHistoryService.recordViewHistory(userId, "artwork", id);
            }

            log.info("作品浏览记录成功: id={}", id);
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            log.warn("记录作品浏览失败: {}", e.getMessage());
            return ApiResponse.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("记录作品浏览异常", e);
            return ApiResponse.serverError("记录作品浏览失败");
        }
    }

    /**
     * 收藏作品
     *
     * @param id 作品ID
     * @param userId 用户ID
     * @return 收藏响应
     */
    @PostMapping("/{id}/collect")
    public ApiResponse<Void> collectArtwork(
            @PathVariable Long id,
            @RequestParam Long userId) {
        log.info("收藏作品: id={}, userId={}", id, userId);

        try {
            // 验证参数
            if (userId == null || userId <= 0) {
                log.warn("用户ID无效: userId={}", userId);
                return ApiResponse.badRequest("用户ID不能为空或小于等于0");
            }

            collectionService.collectArtwork(userId, id);
            log.info("作品收藏成功: id={}, userId={}", id, userId);
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            log.warn("收藏作品失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("收藏作品异常", e);
            return ApiResponse.serverError("收藏作品失败");
        }
    }

    /**
     * 取消收藏作品
     *
     * @param id 作品ID
     * @param userId 用户ID
     * @return 成功响应
     */
    @DeleteMapping("/{id}/collect")
    public ApiResponse<Void> uncollectArtwork(
            @PathVariable Long id,
            @RequestParam Long userId) {
        log.info("取消收藏作品: id={}, userId={}", id, userId);

        try {
            // 验证参数
            if (userId == null || userId <= 0) {
                log.warn("用户ID无效: userId={}", userId);
                return ApiResponse.badRequest("用户ID不能为空或小于等于0");
            }

            collectionService.uncollectArtwork(userId, id);
            log.info("取消收藏成功: id={}, userId={}", id, userId);
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            log.warn("取消收藏失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("取消收藏异常", e);
            return ApiResponse.serverError("取消收藏失败");
        }
    }

}
