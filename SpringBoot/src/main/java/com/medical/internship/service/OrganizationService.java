package com.medical.internship.service;

import com.medical.internship.dto.OrganizationCreateRequest;
import com.medical.internship.dto.OrganizationResponse;
import com.medical.internship.dto.OrganizationUpdateRequest;
import com.medical.internship.dto.UserResponse;

import java.util.List;

/**
 * 组织服务接口
 */
public interface OrganizationService {
    
    /**
     * 获取所有组织
     */
    List<OrganizationResponse> getAllOrganizations();
    
    /**
     * 创建组织
     */
    OrganizationResponse createOrganization(OrganizationCreateRequest request);
    
    /**
     * 更新组织信息
     */
    OrganizationResponse updateOrganization(Long organizationId, OrganizationUpdateRequest request);
    
    /**
     * 获取组织内的用户列表
     */
    List<UserResponse> getOrganizationUsers(Long organizationId);
    
    /**
     * 根据ID获取组织
     */
    OrganizationResponse getOrganizationById(Long organizationId);
}
