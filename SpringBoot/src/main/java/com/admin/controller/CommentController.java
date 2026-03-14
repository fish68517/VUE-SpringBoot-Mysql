package com.admin.controller;

import com.admin.dto.CommentDTO;
import com.admin.entity.Comment;
import com.admin.service.CommentService;
import com.admin.vo.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ApiResponse<CommentDTO> createComment(@RequestBody Comment comment) {
        CommentDTO createdComment = commentService.createComment(comment);
        if (createdComment == null) {
            return ApiResponse.error(400, "Create comment failed");
        }
        return ApiResponse.success("Create comment success", createdComment);
    }

    @GetMapping("/{id}")
    public ApiResponse<CommentDTO> getCommentById(@PathVariable Long id) {
        CommentDTO commentDTO = commentService.getCommentById(id);
        if (commentDTO == null) {
            return ApiResponse.error(404, "Comment not found");
        }
        return ApiResponse.success("Get comment success", commentDTO);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Object> deleteComment(@PathVariable Long id) {
        boolean success = commentService.deleteComment(id);
        if (!success) {
            return ApiResponse.error(404, "Comment not found");
        }
        return ApiResponse.success("Delete comment success", null);
    }
}
