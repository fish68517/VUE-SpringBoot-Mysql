package com.xingluo.petshop.controller;

import com.xingluo.petshop.common.ApiResponse;
import com.xingluo.petshop.dto.*;
import com.xingluo.petshop.entity.CommunityPost;
import com.xingluo.petshop.entity.CommunityReply;
import com.xingluo.petshop.service.CommunityPostService;
import com.xingluo.petshop.service.CommunityReplyService;
import com.xingluo.petshop.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 社区控制器
 */
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {
    
    private final CommunityPostService communityPostService;
    private final CommunityReplyService communityReplyService;
    private final PostLikeService postLikeService;
    
    /**
     * 发布帖子
     */
    @PostMapping("/post")
    public ApiResponse<CommunityPost> createPost(
            @RequestHeader("userId") Long userId,
            @RequestBody CreatePostDTO dto) {
        CommunityPost post = communityPostService.createPost(
                userId, 
                dto.getTitle(), 
                dto.getContent(), 
                dto.getImages()
        );
        return ApiResponse.ok(post);
    }
    
    /**
     * 获取帖子列表（分页）
     */
    @GetMapping("/post/list")
    public ApiResponse<Map<String, Object>> getPostList(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<CommunityPost> postPage = communityPostService.getPostList(page, size);
        
        Map<String, Object> result = new HashMap<>();
        result.put("content", postPage.getContent().stream()
                .map(this::convertToPostVO)
                .collect(Collectors.toList()));
        result.put("totalElements", postPage.getTotalElements());
        result.put("totalPages", postPage.getTotalPages());
        result.put("currentPage", postPage.getNumber());
        
        return ApiResponse.ok(result);
    }
    
    /**
     * 获取帖子详情
     */
    @GetMapping("/post/{id}")
    public ApiResponse<PostVO> getPostDetail(
            @PathVariable Long id,
            @RequestHeader(value = "userId", required = false) Long userId) {
        // 增加浏览数
        communityPostService.incrementViews(id);
        
        CommunityPost post = communityPostService.getPostDetail(id);
        PostVO postVO = convertToPostVO(post);
        
        // 如果用户已登录，查询是否点赞
        if (userId != null) {
            postVO.setIsLiked(postLikeService.isLiked(id, userId));
        } else {
            postVO.setIsLiked(false);
        }
        
        return ApiResponse.ok(postVO);
    }
    
    /**
     * 删除帖子
     */
    @DeleteMapping("/post/{id}")
    public ApiResponse<Void> deletePost(
            @PathVariable Long id,
            @RequestHeader("userId") Long userId) {
        communityPostService.deletePost(id, userId);
        return ApiResponse.ok(null);
    }
    
    /**
     * 发布评论
     */
    @PostMapping("/reply")
    public ApiResponse<CommunityReply> createReply(
            @RequestHeader("userId") Long userId,
            @RequestBody CreateReplyDTO dto) {
        CommunityReply reply = communityReplyService.createReply(
                dto.getPostId(),
                userId,
                dto.getContent()
        );
        return ApiResponse.ok(reply);
    }
    
    /**
     * 获取帖子评论列表
     */
    @GetMapping("/reply/{postId}")
    public ApiResponse<List<ReplyVO>> getPostReplies(@PathVariable Long postId) {
        List<CommunityReply> replies = communityReplyService.getPostReplies(postId);
        List<ReplyVO> replyVOs = replies.stream()
                .map(this::convertToReplyVO)
                .collect(Collectors.toList());
        return ApiResponse.ok(replyVOs);
    }
    
    /**
     * 点赞/取消点赞
     */
    @PostMapping("/like/{postId}")
    public ApiResponse<Map<String, Object>> toggleLike(
            @PathVariable Long postId,
            @RequestHeader("userId") Long userId) {
        boolean isLiked = postLikeService.toggleLike(postId, userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("isLiked", isLiked);
        result.put("message", isLiked ? "点赞成功" : "取消点赞成功");
        
        return ApiResponse.ok(result);
    }
    
    /**
     * 转换为 PostVO
     */
    private PostVO convertToPostVO(CommunityPost post) {
        PostVO vo = new PostVO();
        vo.setId(post.getId());
        vo.setUserId(post.getUserId());
        vo.setTitle(post.getTitle());
        vo.setContent(post.getContent());
        vo.setImages(post.getImages());
        vo.setLikes(post.getLikes());
        vo.setViews(post.getViews());
        vo.setCreateTime(post.getCreateTime());
        
        // 如果关联了用户信息
        if (post.getUser() != null) {
            vo.setUsername(post.getUser().getUsername());
            vo.setUserAvatar(post.getUser().getAvatar());
        }
        
        return vo;
    }
    
    /**
     * 转换为 ReplyVO
     */
    private ReplyVO convertToReplyVO(CommunityReply reply) {
        ReplyVO vo = new ReplyVO();
        vo.setId(reply.getId());
        vo.setPostId(reply.getPostId());
        vo.setUserId(reply.getUserId());
        vo.setContent(reply.getContent());
        vo.setCreateTime(reply.getCreateTime());
        
        // 如果关联了用户信息
        if (reply.getUser() != null) {
            vo.setUsername(reply.getUser().getUsername());
            vo.setUserAvatar(reply.getUser().getAvatar());
        }
        
        return vo;
    }
}
