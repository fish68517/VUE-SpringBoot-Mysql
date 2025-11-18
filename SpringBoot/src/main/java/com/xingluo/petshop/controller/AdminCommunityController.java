package com.xingluo.petshop.controller;

import com.xingluo.petshop.common.ApiResponse;
import com.xingluo.petshop.entity.CommunityPost;
import com.xingluo.petshop.service.CommunityPostService;
import com.xingluo.petshop.service.CommunityReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员社区管理控制器
 * 提供社区内容审核功能，包括帖子和评论的管理
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminCommunityController {

    private final CommunityPostService communityPostService;
    private final CommunityReplyService communityReplyService;

    /**
     * 获取帖子列表（分页）
     * GET /api/admin/post/list
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 帖子分页列表
     */
    @GetMapping("/post/list")
    public ApiResponse<Map<String, Object>> getPostList(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        // 查询所有帖子列表（包括已删除的）
        Page<CommunityPost> postPage = communityPostService.getAllPostList(page, size);
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("content", postPage.getContent());
        result.put("totalElements", postPage.getTotalElements());
        result.put("totalPages", postPage.getTotalPages());
        result.put("currentPage", postPage.getNumber());
        result.put("pageSize", postPage.getSize());
        
        return ApiResponse.ok(result);
    }

    /**
     * 删除帖子（管理员）
     * DELETE /api/admin/post/{id}
     * @param id 帖子ID
     * @return 操作结果
     */
    @DeleteMapping("/post/{id}")
    public ApiResponse<String> deletePost(@PathVariable Long id) {
        communityPostService.deletePostByAdmin(id);
        return ApiResponse.ok("帖子已删除");
    }

    /**
     * 删除评论（管理员）
     * DELETE /api/admin/reply/{id}
     * @param id 评论ID
     * @return 操作结果
     */
    @DeleteMapping("/reply/{id}")
    public ApiResponse<String> deleteReply(@PathVariable Long id) {
        communityReplyService.deleteReply(id);
        return ApiResponse.ok("评论已删除");
    }
}
