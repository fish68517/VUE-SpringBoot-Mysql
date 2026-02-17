package com.zhuang.embroidery.controller;

import com.zhuang.embroidery.dto.AdminUserCreateRequest;
import com.zhuang.embroidery.dto.AdminUserUpdateRequest;
import com.zhuang.embroidery.dto.ApiResponse;
import com.zhuang.embroidery.dto.MessagePushRequest;
import com.zhuang.embroidery.dto.OperationLogListResponse;
import com.zhuang.embroidery.dto.UserListResponse;
import com.zhuang.embroidery.dto.UserResponse;
import com.zhuang.embroidery.service.OperationLogService;
import com.zhuang.embroidery.service.UserService;
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
 * 后台用户管理 API 控制器
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final UserService userService;
    private final OperationLogService operationLogService;

    /**
     * 获取用户列表
     *
     * @param pageNum 页码（默认1）
     * @param pageSize 每页数量（默认10）
     * @return 用户列表响应
     */
    @GetMapping
    public ApiResponse<UserListResponse> getUserList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role    ) {
        log.info("获取用户列表: pageNum={}, pageSize={}", pageNum, pageSize);

        try {
            UserListResponse response = userService.getUserList(pageNum, pageSize,keyword,role);
            log.info("成功获取用户列表: total={}", response.getTotal());
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("获取用户列表失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("获取用户列表异常", e);
            return ApiResponse.serverError("获取用户列表失败");
        }
    }

    /**
     * 创建用户
     *
     * @param request 创建请求
     * @return 用户响应
     */
    @PostMapping
    public ApiResponse<UserResponse> createUser(@RequestBody AdminUserCreateRequest request) {
        log.info("创建用户: username={}", request.getUsername());

        try {
            UserResponse userResponse = userService.createUser(request);
            log.info("用户创建成功: userId={}", userResponse.getId());
            return ApiResponse.success(userResponse);
        } catch (IllegalArgumentException e) {
            log.warn("用户创建失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("用户创建异常", e);
            return ApiResponse.serverError("用户创建失败");
        }
    }

    /**
     * 修改用户信息
     *
     * @param userId 用户ID
     * @param request 修改请求
     * @return 用户响应
     */
    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> updateUser(
            @PathVariable Long userId,
            @RequestBody AdminUserUpdateRequest request) {
        log.info("修改用户信息: userId={}", userId);

        try {
            UserResponse userResponse = userService.updateUserByAdmin(userId, request);
            log.info("用户信息修改成功: userId={}", userId);
            return ApiResponse.success(userResponse);
        } catch (IllegalArgumentException e) {
            log.warn("用户信息修改失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("用户信息修改异常", e);
            return ApiResponse.serverError("用户信息修改失败");
        }
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 成功响应
     */
    @DeleteMapping("/{userId}")
    public ApiResponse<Void> deleteUser(@PathVariable Long userId) {
        log.info("删除用户: userId={}", userId);

        try {
            userService.deleteUser(userId);
            log.info("用户删除成功: userId={}", userId);
            return ApiResponse.success();
        } catch (IllegalArgumentException e) {
            log.warn("用户删除失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("用户删除异常", e);
            return ApiResponse.serverError("用户删除失败");
        }
    }

    /**
     * 获取用户行为日志
     *
     * @param userId 用户ID
     * @param pageNum 页码（默认1）
     * @param pageSize 每页数量（默认10）
     * @return 操作日志列表响应
     */
    @GetMapping("/{userId}/logs")
    public ApiResponse<OperationLogListResponse> getUserOperationLogs(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("获取用户行为日志: userId={}, pageNum={}, pageSize={}", userId, pageNum, pageSize);

        try {
            OperationLogListResponse response = operationLogService.getUserOperationLogs(userId, pageNum, pageSize);
            log.info("成功获取用户行为日志: userId={}, total={}", userId, response.getTotal());
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("获取用户行为日志失败: {}", e.getMessage());
            return ApiResponse.badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("获取用户行为日志异常", e);
            return ApiResponse.serverError("获取用户行为日志失败");
        }
    }

    /**
     * 发送消息推送
     *
     * @param request 消息推送请求
     * @return 成功响应
     */
    @PostMapping("/messages")
    public ApiResponse<Void> sendMessagePush(@RequestBody MessagePushRequest request) {
        log.info("发送消息推送: userId={}, title={}", request.getUserId(), request.getTitle());

        try {
            // 验证请求参数
            if (request.getUserId() == null) {
                log.warn("用户ID不能为空");
                return ApiResponse.badRequest("用户ID不能为空");
            }

            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                log.warn("消息标题不能为空");
                return ApiResponse.badRequest("消息标题不能为空");
            }

            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                log.warn("消息内容不能为空");
                return ApiResponse.badRequest("消息内容不能为空");
            }

            // 这里可以集成实际的消息推送服务（如邮件、短信、推送通知等）
            // 目前仅记录日志
            log.info("消息推送已发送: userId={}, title={}, content={}", 
                    request.getUserId(), request.getTitle(), request.getContent());

            return ApiResponse.success();
        } catch (Exception e) {
            log.error("消息推送异常", e);
            return ApiResponse.serverError("消息推送失败");
        }
    }

}
