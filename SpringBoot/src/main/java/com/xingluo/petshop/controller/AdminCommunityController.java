package com.xingluo.petshop.controller;

import com.xingluo.petshop.common.ApiResponse;
import com.xingluo.petshop.dto.PostVO; // 1. 引入 PostVO
import com.xingluo.petshop.entity.CommunityPost;
import com.xingluo.petshop.service.CommunityPostService;
import com.xingluo.petshop.service.CommunityReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils; // 1. 引入 BeanUtils
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员社区管理控制器
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminCommunityController {

    private final CommunityPostService communityPostService;
    private final CommunityReplyService communityReplyService;

    /**
     * 获取帖子列表（分页）
     */
    @GetMapping("/post/list")
    public ApiResponse<Map<String, Object>> getPostList(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        // 查询所有帖子列表
        Page<CommunityPost> postPage = communityPostService.getAllPostList(page, size);

        // 2. 核心修改：将 Entity 转换为 VO
        List<PostVO> postVOList = postPage.getContent().stream()
                .map(this::convertToPostVO)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("content", postVOList); // 返回 VO 列表
        result.put("totalElements", postPage.getTotalElements());
        result.put("totalPages", postPage.getTotalPages());
        result.put("currentPage", postPage.getNumber());
        result.put("pageSize", postPage.getSize());

        return ApiResponse.ok(result);
    }

    /**
     * 辅助方法：实体转 VO
     */
    private PostVO convertToPostVO(CommunityPost post) {
        PostVO vo = new PostVO();
        BeanUtils.copyProperties(post, vo);

        // 手动设置用户信息，避免懒加载错误
        if (post.getUser() != null) {
            vo.setUsername(post.getUser().getUsername());
            vo.setUserAvatar(post.getUser().getAvatar());
        } else {
            vo.setUsername("未知用户");
        }

        return vo;
    }

    /**
     * 删除帖子（管理员）
     */
    @DeleteMapping("/post/{id}")
    public ApiResponse<String> deletePost(@PathVariable Long id) {
        communityPostService.deletePostByAdmin(id);
        return ApiResponse.ok("帖子已删除");
    }

    /**
     * 删除评论（管理员）
     */
    @DeleteMapping("/reply/{id}")
    public ApiResponse<String> deleteReply(@PathVariable Long id) {
        communityReplyService.deleteReply(id);
        return ApiResponse.ok("评论已删除");
    }
}