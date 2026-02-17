package com.naxi.controller;

import com.naxi.common.ApiResponse;
import com.naxi.entity.Collection;
import com.naxi.entity.Pattern;
import com.naxi.entity.User;
import com.naxi.service.CollectionService;
import com.naxi.service.PatternService;
import com.naxi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/collections")
public class CollectionController {
    @Autowired
    private CollectionService collectionService;

    @Autowired
    private UserService userService;

    @Autowired
    private PatternService patternService;

    /**
     * 添加收藏
     * 需求: 20.1
     */
    @PostMapping
    public ApiResponse<?> addCollection(@RequestBody Map<String, Long> request) {
        try {
            Long userId = request.get("userId");
            Long patternId = request.get("patternId");

            if (userId == null) {
                return ApiResponse.error(400, "用户ID不能为空");
            }
            if (patternId == null) {
                return ApiResponse.error(400, "纹样ID不能为空");
            }

            // 验证用户是否存在
            User user = userService.getUserById(userId);
            if (user == null) {
                return ApiResponse.error(1001, "用户不存在");
            }

            // 验证纹样是否存在
            Pattern pattern = patternService.getPatternById(patternId);
            if (pattern == null) {
                return ApiResponse.error(2001, "纹样不存在");
            }

            // 检查是否已收藏
            if (collectionService.isPatternCollected(userId, patternId)) {
                return ApiResponse.error(400, "该纹样已被收藏");
            }

            Collection collection = collectionService.addCollection(userId, patternId);
            return ApiResponse.success("收藏成功", collection);
        } catch (Exception e) {
            return ApiResponse.error(500, "添加收藏失败: " + e.getMessage());
        }
    }

    /**
     * 删除收藏
     * 需求: 20.1
     */
    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteCollection(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ApiResponse.error(400, "收藏ID无效");
            }

            Collection collection = collectionService.getCollectionById(id);
            if (collection == null) {
                return ApiResponse.error(404, "收藏不存在");
            }

            boolean deleted = collectionService.deleteCollection(id);
            if (deleted) {
                return ApiResponse.success("删除收藏成功");
            } else {
                return ApiResponse.error(500, "删除收藏失败");
            }
        } catch (Exception e) {
            return ApiResponse.error(500, "删除收藏失败: " + e.getMessage());
        }
    }

    // 根据 userId 和 patternId 获取收藏详情（用于前端展示）
    @GetMapping("/detail")
    public ApiResponse<?> getCollectionDetail(@RequestParam Long userId, @RequestParam Long patternId) {
        try {
            if (userId == null) {
                return ApiResponse.error(400, "用户ID不能为空");
            }
            if (patternId == null) {
                return ApiResponse.error(400, "纹样ID不能为空");
            }

            // 验证用户是否存在
            User user = userService.getUserById(userId);
            if (user == null) {
                return ApiResponse.error(1001, "用户不存在");
            }

            // 验证纹样是否存在
            Pattern pattern = patternService.getPatternById(patternId);
            if (pattern == null) {
                return ApiResponse.error(2001, "纹样不存在");
            }

            Collection collection = collectionService.getCollectionByUserIdAndPatternId(userId, patternId);
            if (collection == null) {
                return ApiResponse.error(404, "收藏不存在");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("id", collection.getId());
            response.put("userId", collection.getUserId());
            response.put("patternId", collection.getPatternId());
            response.put("createdAt", collection.getCreatedAt());
            response.put("pattern", pattern);

            return ApiResponse.success("获取收藏详情成功", response);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取收藏详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取收藏列表
     * 需求: 20.1
     */
    @GetMapping
    public ApiResponse<?> getCollections(@RequestParam Long userId) {
        try {
            if (userId == null) {
                return ApiResponse.error(400, "用户ID不能为空");
            }

            // 验证用户是否存在
            User user = userService.getUserById(userId);
            if (user == null) {
                return ApiResponse.error(1001, "用户不存在");
            }

            List<Collection> collections = collectionService.getUserCollections(userId);
            
            // 构建响应数据，包含纹样详情
            List<Map<String, Object>> response = new ArrayList<>();
            for (Collection collection : collections) {
                Pattern pattern = patternService.getPatternById(collection.getPatternId());
                if (pattern != null) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", collection.getId());
                    item.put("userId", collection.getUserId());
                    item.put("patternId", collection.getPatternId());
                    item.put("createdAt", collection.getCreatedAt());
                    item.put("pattern", pattern);
                    response.add(item);
                }
            }

            return ApiResponse.success("获取收藏列表成功", response);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取收藏列表失败: " + e.getMessage());
        }
    }
}
