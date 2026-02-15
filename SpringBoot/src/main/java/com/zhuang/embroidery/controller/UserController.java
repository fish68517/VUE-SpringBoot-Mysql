package com.zhuang.embroidery.controller;

import com.zhuang.embroidery.dto.ApiResponse;
import com.zhuang.embroidery.dto.CollectionListResponse;
import com.zhuang.embroidery.dto.UserLoginRequest;
import com.zhuang.embroidery.dto.UserRegisterRequest;
import com.zhuang.embroidery.dto.UserResponse;
import com.zhuang.embroidery.dto.UserUpdateRequest;
import com.zhuang.embroidery.dto.ViewHistoryListResponse;
import com.zhuang.embroidery.service.CollectionService;
import com.zhuang.embroidery.service.UserService;
import com.zhuang.embroidery.service.ViewHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户相关 API 控制器
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final CollectionService collectionService;
    private final ViewHistoryService viewHistoryService;

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 用户响应
     */
    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@RequestBody UserRegisterRequest request) {
        log.info("用户注册请求: username={}", request.getUsername());

        request.setConfirmPassword(request.getPassword());
        try {
            UserResponse userResponse = userService.register(request);
            log.info("用户注册成功: userId={}", userResponse.getId());
            return ApiResponse.success(userResponse);
        } catch (IllegalArgumentException e) {
            log.warn("用户注册失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("用户注册异常", e);
            return ApiResponse.serverError("用户注册失败");
        }
    }

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 用户响应
     */
    @PostMapping("/login")
    public ApiResponse<UserResponse> login(@RequestBody UserLoginRequest request) {
        log.info("用户登录请求: username={}", request.getUsername());

        try {
            UserResponse userResponse = userService.login(request);
            log.info("用户登录成功: userId={}", userResponse.getId());
            return ApiResponse.success(userResponse);
        } catch (IllegalArgumentException e) {
            log.warn("用户登录失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("用户登录异常", e);
            return ApiResponse.serverError("用户登录失败");
        }
    }

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户响应
     */
    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUserInfo(@PathVariable Long userId) {
        log.info("获取用户信息: userId={}", userId);

        try {
            UserResponse userResponse = userService.getUserInfo(userId);
            log.info("成功获取用户信息: userId={}", userId);
            return ApiResponse.success(userResponse);
        } catch (IllegalArgumentException e) {
            log.warn("获取用户信息失败: {}", e.getMessage());
            return ApiResponse.notFound(e.getMessage());
        } catch (Exception e) {
            log.error("获取用户信息异常", e);
            return ApiResponse.serverError("获取用户信息失败");
        }
    }

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param request 更新请求
     * @return 用户响应
     */
    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> updateUserInfo(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        log.info("更新用户信息: userId={}", userId);

        try {
            UserResponse userResponse = userService.updateUserInfo(userId, request);
            log.info("用户信息更新成功: userId={}", userId);
            return ApiResponse.success(userResponse);
        } catch (IllegalArgumentException e) {
            log.warn("用户信息更新失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("用户信息更新异常", e);
            return ApiResponse.serverError("用户信息更新失败");
        }
    }

    /**
     * 获取用户收藏列表
     *
     * @param userId 用户ID
     * @param pageNum 页码（默认1）
     * @param pageSize 每页数量（默认10）
     * @return 收藏列表响应
     */
    @GetMapping("/{userId}/collections")
    public ApiResponse<CollectionListResponse> getUserCollections(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("获取用户收藏列表: userId={}, pageNum={}, pageSize={}", userId, pageNum, pageSize);

        try {
            CollectionListResponse response = collectionService.getUserCollections(userId, pageNum, pageSize);
            log.info("成功获取用户收藏列表: userId={}, total={}", userId, response.getTotal());
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("获取用户收藏列表失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("获取用户收藏列表异常", e);
            return ApiResponse.serverError("获取用户收藏列表失败");
        }
    }

    /**
     * 获取用户浏览历史
     *
     * @param userId 用户ID
     * @param pageNum 页码（默认1）
     * @param pageSize 每页数量（默认10）
     * @return 浏览历史列表响应
     */
    @GetMapping("/{userId}/history")
    public ApiResponse<ViewHistoryListResponse> getUserViewHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("获取用户浏览历史: userId={}, pageNum={}, pageSize={}", userId, pageNum, pageSize);

        try {
            ViewHistoryListResponse response = viewHistoryService.getUserViewHistory(userId, pageNum, pageSize);
            log.info("成功获取用户浏览历史: userId={}, total={}", userId, response.getTotal());
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("获取用户浏览历史失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("获取用户浏览历史异常", e);
            return ApiResponse.serverError("获取用户浏览历史失败");
        }
    }

}
