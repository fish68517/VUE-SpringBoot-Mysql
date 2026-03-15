package server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.dto.CommentDTO;
import server.model.Comment;
import server.model.CommentLike;
import server.service.CommentService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 获取指定菜谱的所有评论
     * @param recipeId 菜谱ID
     * @return 评论列表
     */
    @GetMapping("/recipe/{recipeId}")
    public List<CommentDTO> getCommentsByRecipe(@PathVariable Integer recipeId) {
        return commentService.getCommentsByRecipe(recipeId);
    }

    /**
     * 添加评论
     * @param userId 用户ID (从请求属性中获取)
     * @param comment 评论数据
     * @return 添加的评论
     */
    @PostMapping
    public Comment addComment(
            @RequestAttribute Integer userId,
            @RequestBody Comment comment) {
        comment.setUserId(userId);
        return commentService.addComment(comment);
    }

    /**
     * 删除评论 (仅允许评论作者或管理员)
     * @param userId 用户ID (从请求属性中获取)
     * @param commentId 评论ID
     * @return 删除结果
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @RequestAttribute Integer userId,
            @PathVariable Integer commentId) {
        boolean isDeleted = commentService.deleteComment(commentId, userId);
        if (isDeleted) {
            return ResponseEntity.ok().body(Map.of("message", "评论删除成功"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "评论删除失败，无权限或评论不存在"));
        }
    }

    /**
     * 点赞评论
     * @param userId 用户ID (从请求属性中获取)
     * @param commentId 评论ID
     * @return 点赞结果
     */
    @PostMapping("/{commentId}/like")
    public ResponseEntity<?> likeComment(
            @RequestAttribute Integer userId,
            @PathVariable Integer commentId) {
        CommentLike like = commentService.likeComment(commentId, userId);
        return ResponseEntity.ok().body(Map.of("liked", true, "id", like.getId()));
    }

    /**
     * 取消点赞评论
     * @param userId 用户ID (从请求属性中获取)
     * @param commentId 评论ID
     * @return 取消点赞结果
     */
    @DeleteMapping("/{commentId}/like")
    public ResponseEntity<?> unlikeComment(
            @RequestAttribute Integer userId,
            @PathVariable Integer commentId) {
        boolean isUnliked = commentService.unlikeComment(commentId, userId);
        if (isUnliked) {
            return ResponseEntity.ok().body(Map.of("unliked", true));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "取消点赞失败"));
        }
    }

    /**
     * 检查用户是否已点赞评论
     * @param userId 用户ID (从请求属性中获取)
     * @param commentId 评论ID
     * @return 是否已点赞
     */
    @GetMapping("/{commentId}/like")
    public ResponseEntity<Boolean> checkLike(
            @RequestAttribute Integer userId,
            @PathVariable Integer commentId) {
        boolean isLiked = commentService.isCommentLiked(commentId, userId);
        return ResponseEntity.ok(isLiked);
    }
} 