package com.zhuang.embroidery.controller;

import com.zhuang.embroidery.dto.ActivityCreateRequest;
import com.zhuang.embroidery.dto.ActivityResponse;
import com.zhuang.embroidery.dto.ActivityUpdateRequest;
import com.zhuang.embroidery.dto.ApiResponse;
import com.zhuang.embroidery.dto.ArtworkCreateRequest;
import com.zhuang.embroidery.dto.ArtworkResponse;
import com.zhuang.embroidery.dto.ArtworkUpdateRequest;
import com.zhuang.embroidery.dto.NewsCreateRequest;
import com.zhuang.embroidery.dto.NewsResponse;
import com.zhuang.embroidery.dto.NewsUpdateRequest;
import com.zhuang.embroidery.service.ActivityService;
import com.zhuang.embroidery.service.ArtworkService;
import com.zhuang.embroidery.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 后台资源管理 API 控制器
 */
@RestController
@RequestMapping("/api/admin/resources")
@RequiredArgsConstructor
@Slf4j
public class AdminResourceController {

    private final ArtworkService artworkService;
    private final NewsService newsService;
    private final ActivityService activityService;

    // ==================== 作品管理接口 ====================

    /**
     * 上传作品
     *
     * @param request 作品创建请求
     * @return 作品响应
     */
    @PostMapping("/artworks")
    public ApiResponse<ArtworkResponse> uploadArtwork(@RequestBody ArtworkCreateRequest request) {
        log.info("上传作品: title={}", request.getTitle());

        try {
            ArtworkResponse response = artworkService.createArtwork(request);
            log.info("作品上传成功: artworkId={}", response.getId());
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("作品上传失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("作品上传异常", e);
            return ApiResponse.serverError("作品上传失败");
        }
    }

    /**
     * 编辑作品
     *
     * @param artworkId 作品ID
     * @param request 作品更新请求
     * @return 作品响应
     */
    @PutMapping("/artworks/{artworkId}")
    public ApiResponse<ArtworkResponse> editArtwork(
            @PathVariable Long artworkId,
            @RequestBody ArtworkUpdateRequest request) {
        log.info("编辑作品: artworkId={}", artworkId);

        try {
            ArtworkResponse response = artworkService.updateArtwork(artworkId, request);
            log.info("作品编辑成功: artworkId={}", artworkId);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("作品编辑失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("作品编辑异常", e);
            return ApiResponse.serverError("作品编辑失败");
        }
    }

    /**
     * 删除作品
     *
     * @param artworkId 作品ID
     * @return 成功响应
     */
    @DeleteMapping("/artworks/{artworkId}")
    public ApiResponse<Void> deleteArtwork(@PathVariable Long artworkId) {
        log.info("删除作品: artworkId={}", artworkId);

        try {
            artworkService.deleteArtwork(artworkId);
            log.info("作品删除成功: artworkId={}", artworkId);
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            log.warn("作品删除失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("作品删除异常", e);
            return ApiResponse.serverError("作品删除失败");
        }
    }

    /**
     * 审核作品（批准）
     *
     * @param artworkId 作品ID
     * @return 作品响应
     */
    @PostMapping("/artworks/{artworkId}/approve")
    public ApiResponse<ArtworkResponse> approveArtwork(@PathVariable Long artworkId) {
        log.info("审核作品（批准）: artworkId={}", artworkId);

        try {
            ArtworkResponse response = artworkService.approveArtwork(artworkId);
            log.info("作品批准成功: artworkId={}", artworkId);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("作品批准失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("作品批准异常", e);
            return ApiResponse.serverError("作品批准失败");
        }
    }

    /**
     * 拒绝作品
     *
     * @param artworkId 作品ID
     * @return 作品响应
     */
    @PostMapping("/artworks/{artworkId}/reject")
    public ApiResponse<ArtworkResponse> rejectArtwork(@PathVariable Long artworkId) {
        log.info("拒绝作品: artworkId={}", artworkId);

        try {
            ArtworkResponse response = artworkService.rejectArtwork(artworkId);
            log.info("作品拒绝成功: artworkId={}", artworkId);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("作品拒绝失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("作品拒绝异常", e);
            return ApiResponse.serverError("作品拒绝失败");
        }
    }

    /**
     * 下架作品
     *
     * @param artworkId 作品ID
     * @return 作品响应
     */
    @PostMapping("/artworks/{artworkId}/offline")
    public ApiResponse<ArtworkResponse> offlineArtwork(@PathVariable Long artworkId) {
        log.info("下架作品: artworkId={}", artworkId);

        try {
            ArtworkResponse response = artworkService.offlineArtwork(artworkId);
            log.info("作品下架成功: artworkId={}", artworkId);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("作品下架失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("作品下架异常", e);
            return ApiResponse.serverError("作品下架失败");
        }
    }

    // ==================== 资讯管理接口 ====================

    /**
     * 发布资讯
     *
     * @param request 资讯创建请求
     * @return 资讯响应
     */
    @PostMapping("/news")
    public ApiResponse<NewsResponse> publishNews(@RequestBody NewsCreateRequest request) {
        log.info("发布资讯: title={}", request.getTitle());

        try {
            NewsResponse response = newsService.createNews(request);
            log.info("资讯发布成功: newsId={}", response.getId());
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("资讯发布失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("资讯发布异常", e);
            return ApiResponse.serverError("资讯发布失败");
        }
    }

    /**
     * 编辑资讯
     *
     * @param newsId 资讯ID
     * @param request 资讯更新请求
     * @return 资讯响应
     */
    @PutMapping("/news/{newsId}")
    public ApiResponse<NewsResponse> editNews(
            @PathVariable Long newsId,
            @RequestBody NewsUpdateRequest request) {
        log.info("编辑资讯: newsId={}", newsId);

        try {
            NewsResponse response = newsService.updateNews(newsId, request);
            log.info("资讯编辑成功: newsId={}", newsId);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("资讯编辑失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("资讯编辑异常", e);
            return ApiResponse.serverError("资讯编辑失败");
        }
    }

    /**
     * 删除资讯
     *
     * @param newsId 资讯ID
     * @return 成功响应
     */
    @DeleteMapping("/news/{newsId}")
    public ApiResponse<Void> deleteNews(@PathVariable Long newsId) {
        log.info("删除资讯: newsId={}", newsId);

        try {
            newsService.deleteNews(newsId);
            log.info("资讯删除成功: newsId={}", newsId);
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            log.warn("资讯删除失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("资讯删除异常", e);
            return ApiResponse.serverError("资讯删除失败");
        }
    }

    // ==================== 活动管理接口 ====================

    /**
     * 发布活动
     *
     * @param request 活动创建请求
     * @return 活动响应
     */
    @PostMapping("/activities")
    public ApiResponse<ActivityResponse> publishActivity(@RequestBody ActivityCreateRequest request) {
        log.info("发布活动: title={}", request.getTitle());

        try {
            ActivityResponse response = activityService.createActivity(request);
            log.info("活动发布成功: activityId={}", response.getId());
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("活动发布失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("活动发布异常", e);
            return ApiResponse.serverError("活动发布失败");
        }
    }

    /**
     * 编辑活动
     *
     * @param activityId 活动ID
     * @param request 活动更新请求
     * @return 活动响应
     */
    @PutMapping("/activities/{activityId}")
    public ApiResponse<ActivityResponse> editActivity(
            @PathVariable Long activityId,
            @RequestBody ActivityUpdateRequest request) {
        log.info("编辑活动: activityId={}", activityId);

        try {
            ActivityResponse response = activityService.updateActivity(activityId, request);
            log.info("活动编辑成功: activityId={}", activityId);
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("活动编辑失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("活动编辑异常", e);
            return ApiResponse.serverError("活动编辑失败");
        }
    }

    /**
     * 删除活动
     *
     * @param activityId 活动ID
     * @return 成功响应
     */
    @DeleteMapping("/activities/{activityId}")
    public ApiResponse<Void> deleteActivity(@PathVariable Long activityId) {
        log.info("删除活动: activityId={}", activityId);

        try {
            activityService.deleteActivity(activityId);
            log.info("活动删除成功: activityId={}", activityId);
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            log.warn("活动删除失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("活动删除异常", e);
            return ApiResponse.serverError("活动删除失败");
        }
    }





    /**
     * 获取资讯分页列表
     */
    @GetMapping("/news")
    public ApiResponse<Map<String, Object>> getNewsList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("查询资讯列表: pageNum={}, pageSize={}", pageNum, pageSize);
        try {
            Map<String, Object> pageData = newsService.getNewsPage(pageNum, pageSize);
            return ApiResponse.success(pageData);
        } catch (Exception e) {
            log.error("查询资讯列表异常", e);
            return ApiResponse.serverError("获取列表失败");
        }
    }

    /**
     * 获取活动分页列表
     */
    @GetMapping("/activities")
    public ApiResponse<Map<String, Object>> getActivityList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("查询活动列表: pageNum={}, pageSize={}", pageNum, pageSize);
        try {
            Map<String, Object> pageData = activityService.getActivityPage(pageNum, pageSize);
            return ApiResponse.success(pageData);
        } catch (Exception e) {
            log.error("查询活动列表异常", e);
            return ApiResponse.serverError("获取列表失败");
        }
    }

    /**
     * 获取作品分页列表
     */
    @GetMapping("/artworks")
    public ApiResponse<Map<String, Object>> getArtworkList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("查询作品列表: pageNum={}, pageSize={}", pageNum, pageSize);
        try {
            Map<String, Object> pageData = artworkService.getArtworkPage(pageNum, pageSize);
            return ApiResponse.success(pageData);
        } catch (Exception e) {
            log.error("查询作品列表异常", e);
            return ApiResponse.serverError("获取列表失败");
        }
    }
}
