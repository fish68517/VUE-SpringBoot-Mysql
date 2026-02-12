package com.medical.internship.controller;

import com.medical.internship.common.AccessDeniedException;
import com.medical.internship.common.ApiResponse;
import com.medical.internship.common.SessionContext;
import com.medical.internship.dto.OrganizationCreateRequest;
import com.medical.internship.dto.OrganizationResponse;
import com.medical.internship.dto.OrganizationUpdateRequest;
import com.medical.internship.dto.UserResponse;
import com.medical.internship.service.OrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 组织控制器 - 系统管理员专用
 */
@Slf4j
@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {
    
    @Autowired
    private OrganizationService organizationService;
    
    /**
     * 获取所有组织
     * GET /api/organizations
     */
    @GetMapping
    public ApiResponse<List<OrganizationResponse>> getAllOrganizations() {
        log.info("获取所有组织");
        verifyAdminRole();
        List<OrganizationResponse> organizations = organizationService.getAllOrganizations();
        return ApiResponse.success(organizations);
    }
    
    /**
     * 创建组织
     * POST /api/organizations
     */
    @PostMapping
    public ApiResponse<OrganizationResponse> createOrganization(@Valid @RequestBody OrganizationCreateRequest request) {
        log.info("创建组织: {}", request.getCode());
        verifyAdminRole();
        OrganizationResponse response = organizationService.createOrganization(request);
        return ApiResponse.success("组织创建成功", response);
    }
    
    /**
     * 更新组织信息
     * PUT /api/organizations/{id}
     */
    @PutMapping("/{id}")
    public ApiResponse<OrganizationResponse> updateOrganization(
            @PathVariable Long id,
            @Valid @RequestBody OrganizationUpdateRequest request) {
        log.info("更新组织: {}", id);
        verifyAdminRole();
        OrganizationResponse response = organizationService.updateOrganization(id, request);
        return ApiResponse.success("组织更新成功", response);
    }
    
    /**
     * 获取组织内的用户列表
     * GET /api/organizations/{id}/users
     */
    @GetMapping("/{id}/users")
    public ApiResponse<List<UserResponse>> getOrganizationUsers(@PathVariable Long id) {
        log.info("获取组织内用户: {}", id);
        verifyAdminRole();
        List<UserResponse> users = organizationService.getOrganizationUsers(id);
        return ApiResponse.success(users);
    }
    
    /**
     * 验证当前用户是否为系统管理员
     */
    private void verifyAdminRole() {
        String role = SessionContext.getCurrentUserRole();
        if (!"ADMIN".equals(role)) {
            throw new AccessDeniedException("只有系统管理员可以访问此接口");
        }
    }
}
