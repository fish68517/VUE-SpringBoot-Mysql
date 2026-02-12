package com.medical.internship.controller;

import com.medical.internship.common.ApiResponse;
import com.medical.internship.dto.PostCreateRequest;
import com.medical.internship.dto.PostResponse;
import com.medical.internship.dto.PostStatusUpdateRequest;
import com.medical.internship.dto.PostUpdateRequest;
import com.medical.internship.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 岗位管理控制器
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    
    private final PostService postService;
    
    /**
     * 获取岗位列表（根据用户身份进行数据隔离）
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostResponse>>> getPostList() {
        List<PostResponse> posts = postService.getPostList();
        return ResponseEntity.ok(ApiResponse.success(posts));
    }
    
    /**
     * 获取岗位详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> getPostDetail(@PathVariable Long id) {
        PostResponse post = postService.getPostDetail(id);
        return ResponseEntity.ok(ApiResponse.success(post));
    }
    
    /**
     * 创建岗位（医院管理员）
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(@Valid @RequestBody PostCreateRequest request) {
        PostResponse post = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("岗位创建成功", post));
    }
    
    /**
     * 更新岗位（医院管理员）
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostUpdateRequest request) {
        PostResponse post = postService.updatePost(id, request);
        return ResponseEntity.ok(ApiResponse.success("岗位更新成功", post));
    }
    
    /**
     * 更新岗位状态（上下架）
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PostResponse>> updatePostStatus(
            @PathVariable Long id,
            @Valid @RequestBody PostStatusUpdateRequest request) {
        PostResponse post = postService.updatePostStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("岗位状态更新成功", post));
    }
}
