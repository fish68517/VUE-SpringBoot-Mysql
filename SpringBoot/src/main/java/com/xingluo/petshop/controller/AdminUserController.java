package com.xingluo.petshop.controller;

import com.xingluo.petshop.common.ApiResponse;
import com.xingluo.petshop.dto.UserVO;
import com.xingluo.petshop.entity.User;
import com.xingluo.petshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员用户管理控制器
 * 提供用户列表查询、搜索、启用/禁用等管理功能
 */
@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    /**
     * 获取用户列表（分页）
     * GET /api/admin/user/list
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 用户分页列表
     */
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getUserList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // 创建分页参数，按创建时间倒序
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        
        // 查询用户列表
        Page<User> userPage = userService.getAllUsers(pageable);
        
        // 转换为VO
        Page<UserVO> userVOPage = userPage.map(this::convertToVO);
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("content", userVOPage.getContent());
        result.put("totalElements", userVOPage.getTotalElements());
        result.put("totalPages", userVOPage.getTotalPages());
        result.put("currentPage", userVOPage.getNumber());
        result.put("pageSize", userVOPage.getSize());
        
        return ApiResponse.ok(result);
    }

    /**
     * 搜索用户
     * GET /api/admin/user/search
     * @param keyword 搜索关键词（用户名、昵称、邮箱、手机号）
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 用户分页列表
     */
    @GetMapping("/search")
    public ApiResponse<Map<String, Object>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // 创建分页参数，按创建时间倒序
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        
        // 搜索用户
        Page<User> userPage = userService.searchUsers(keyword, pageable);
        
        // 转换为VO
        Page<UserVO> userVOPage = userPage.map(this::convertToVO);
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("content", userVOPage.getContent());
        result.put("totalElements", userVOPage.getTotalElements());
        result.put("totalPages", userVOPage.getTotalPages());
        result.put("currentPage", userVOPage.getNumber());
        result.put("pageSize", userVOPage.getSize());
        result.put("keyword", keyword);
        
        return ApiResponse.ok(result);
    }

    /**
     * 启用/禁用用户
     * PUT /api/admin/user/{id}/status
     * @param id 用户ID
     * @param status 状态（0禁用/1启用）
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    public ApiResponse<String> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        
        userService.updateUserStatus(id, status);
        
        String message = status == 1 ? "用户已启用" : "用户已禁用";
        return ApiResponse.ok(message);
    }

    /**
     * 将User实体转换为UserVO
     * 不包含密码等敏感信息
     */
    private UserVO convertToVO(User user) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
}
