package com.travelMemory.controller;

import com.travelMemory.common.ApiResponse;
import com.travelMemory.dto.CommentResponse;
import com.travelMemory.dto.LikeResponse;
import com.travelMemory.service.AdminSocialService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/social")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
@PreAuthorize("hasRole('ADMIN')")
public class AdminSocialController {

    private final AdminSocialService adminSocialService;

    @GetMapping("/comments")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CommentResponse> comments = adminSocialService.getComments(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Long id) {
        adminSocialService.deleteComment(id);
        return ResponseEntity.ok(ApiResponse.success("评论删除成功", null));
    }

    @GetMapping("/likes")
    public ResponseEntity<ApiResponse<Page<LikeResponse>>> getLikes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<LikeResponse> likes = adminSocialService.getLikes(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return ResponseEntity.ok(ApiResponse.success(likes));
    }

    @DeleteMapping("/likes/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLike(@PathVariable Long id) {
        adminSocialService.deleteLike(id);
        return ResponseEntity.ok(ApiResponse.success("点赞记录删除成功", null));
    }
}
