package com.admin.controller;

import com.admin.dto.PostDTO;
import com.admin.entity.Post;
import com.admin.service.PostService;
import com.admin.vo.ApiResponse;
import com.admin.vo.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public ApiResponse<PageResponse<PostDTO>> getPostList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        if (page < 1 || pageSize < 1 || pageSize > 100) {
            return ApiResponse.error(400, "Invalid paging params");
        }
        return ApiResponse.success("Get posts success", postService.getPostList(page, pageSize));
    }

    @PostMapping
    public ApiResponse<PostDTO> createPost(@RequestBody Post post) {
        PostDTO createdPost = postService.createPost(post);
        if (createdPost == null) {
            return ApiResponse.error(400, "Create post failed");
        }
        return ApiResponse.success("Create post success", createdPost);
    }

    @PostMapping("/{id}/like")
    public ApiResponse<PostDTO> likePost(@PathVariable Long id) {
        PostDTO updatedPost = postService.likePost(id);
        if (updatedPost == null) {
            return ApiResponse.error(404, "Post not found");
        }
        return ApiResponse.success("Like post success", updatedPost);
    }

    @GetMapping("/{id}")
    public ApiResponse<PostDTO> getPostById(@PathVariable Long id) {
        PostDTO postDTO = postService.getPostById(id);
        if (postDTO == null) {
            return ApiResponse.error(404, "Post not found");
        }
        return ApiResponse.success("Get post success", postDTO);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Object> deletePost(@PathVariable Long id) {
        boolean success = postService.deletePost(id);
        if (!success) {
            return ApiResponse.error(404, "Post not found");
        }
        return ApiResponse.success("Delete post success", null);
    }
}
