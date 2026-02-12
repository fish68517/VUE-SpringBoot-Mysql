package com.medical.internship.service.impl;

import com.medical.internship.common.BusinessException;
import com.medical.internship.common.ResourceNotFoundException;
import com.medical.internship.dto.OrganizationCreateRequest;
import com.medical.internship.dto.OrganizationResponse;
import com.medical.internship.dto.OrganizationUpdateRequest;
import com.medical.internship.dto.UserResponse;
import com.medical.internship.entity.Organization;
import com.medical.internship.entity.User;
import com.medical.internship.repository.OrganizationRepository;
import com.medical.internship.repository.UserRepository;
import com.medical.internship.service.OrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 组织服务实现类
 */
@Slf4j
@Service
@Transactional
public class OrganizationServiceImpl implements OrganizationService {
    
    @Autowired
    private OrganizationRepository organizationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 获取所有组织
     */
    @Override
    @Transactional(readOnly = true)
    public List<OrganizationResponse> getAllOrganizations() {
        log.info("获取所有组织");
        List<Organization> organizations = organizationRepository.findAll();
        return organizations.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 创建组织
     */
    @Override
    public OrganizationResponse createOrganization(OrganizationCreateRequest request) {
        log.info("创建组织: {}", request.getCode());
        
        // 检查组织代码是否已存在
        if (organizationRepository.findByCode(request.getCode()).isPresent()) {
            throw new BusinessException("组织代码已存在");
        }
        
        // 创建新组织
        Organization organization = Organization.builder()
                .name(request.getName())
                .type(request.getType())
                .code(request.getCode())
                .build();
        
        Organization savedOrganization = organizationRepository.save(organization);
        log.info("组织创建成功: {}", savedOrganization.getId());
        
        return convertToResponse(savedOrganization);
    }
    
    /**
     * 更新组织信息
     */
    @Override
    public OrganizationResponse updateOrganization(Long organizationId, OrganizationUpdateRequest request) {
        log.info("更新组织: {}", organizationId);
        
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("组织不存在"));
        
        organization.setName(request.getName());
        Organization updatedOrganization = organizationRepository.save(organization);
        
        log.info("组织更新成功: {}", organizationId);
        return convertToResponse(updatedOrganization);
    }
    
    /**
     * 获取组织内的用户列表
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getOrganizationUsers(Long organizationId) {
        log.info("获取组织内用户: {}", organizationId);
        
        // 验证组织是否存在
        if (!organizationRepository.existsById(organizationId)) {
            throw new ResourceNotFoundException("组织不存在");
        }
        
        List<User> users = userRepository.findByOrganizationId(organizationId);
        return users.stream()
                .map(this::convertUserToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据ID获取组织
     */
    @Override
    @Transactional(readOnly = true)
    public OrganizationResponse getOrganizationById(Long organizationId) {
        log.info("获取组织详情: {}", organizationId);
        
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("组织不存在"));
        
        return convertToResponse(organization);
    }
    
    /**
     * 将Organization实体转换为OrganizationResponse DTO
     */
    private OrganizationResponse convertToResponse(Organization organization) {
        return OrganizationResponse.builder()
                .id(organization.getId())
                .name(organization.getName())
                .type(organization.getType())
                .code(organization.getCode())
                .build();
    }
    
    /**
     * 将User实体转换为UserResponse DTO
     */
    private UserResponse convertUserToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .organization(convertToResponse(user.getOrganization()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
