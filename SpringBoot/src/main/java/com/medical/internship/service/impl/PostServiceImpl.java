package com.medical.internship.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.internship.common.AccessDeniedException;
import com.medical.internship.common.BusinessException;
import com.medical.internship.common.ResourceNotFoundException;
import com.medical.internship.common.SessionContext;
import com.medical.internship.dto.OrganizationResponse;
import com.medical.internship.dto.PostCreateRequest;
import com.medical.internship.dto.PostResponse;
import com.medical.internship.dto.PostStatusUpdateRequest;
import com.medical.internship.dto.PostUpdateRequest;
import com.medical.internship.entity.Organization;
import com.medical.internship.entity.Post;
import com.medical.internship.repository.ApplicationRepository;
import com.medical.internship.repository.OrganizationRepository;
import com.medical.internship.repository.PostRepository;
import com.medical.internship.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 岗位服务实现类
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {
    
    private final PostRepository postRepository;
    private final ApplicationRepository applicationRepository;
    private final OrganizationRepository organizationRepository;
    private final ObjectMapper objectMapper;
    
    @Override
    public List<PostResponse> getPostList() {
        String userRole = SessionContext.getCurrentUserRole();
        Long userOrgId = SessionContext.getCurrentOrganizationId();
        
        List<Post> posts = new ArrayList<>();
        
        // 系统管理员：查看所有已发布的岗位
        if ("ADMIN".equals(userRole)) {
            posts = postRepository.findByStatus("PUBLISHED");
        }
        // 医院管理员：查看本院所有岗位
        else if ("HOSPITAL_ADMIN".equals(userRole)) {
            posts = postRepository.findByHospitalId(userOrgId);
        }
        // 学校管理员：查看面向本校的已发布岗位
        else if ("SCHOOL_ADMIN".equals(userRole)) {
            posts = postRepository.findByStatus("PUBLISHED");
            posts = filterPostsBySchool(posts, userOrgId);
        }
        // 学生：查看面向本校的已发布岗位
        else if ("STUDENT".equals(userRole)) {
            posts = postRepository.findByStatus("PUBLISHED");
            posts = filterPostsBySchool(posts, userOrgId);
        }
        // 带教老师：查看本院的已发布岗位
        else if ("TEACHER".equals(userRole)) {
            posts = postRepository.findByHospitalId(userOrgId);
        }
        
        return posts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public PostResponse getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("岗位不存在"));
        
        // 验证访问权限
        validatePostAccess(post);
        
        return convertToResponse(post);
    }
    
    @Override
    public PostResponse createPost(PostCreateRequest request) {
        String userRole = SessionContext.getCurrentUserRole();
        Long userOrgId = SessionContext.getCurrentOrganizationId();
        
        // 只有医院管理员可以创建岗位
        if (!"HOSPITAL_ADMIN".equals(userRole)) {
            throw new AccessDeniedException("只有医院管理员可以创建岗位");
        }
        
        // 获取医院组织
        Organization hospital = organizationRepository.findById(userOrgId)
                .orElseThrow(() -> new ResourceNotFoundException("医院不存在"));
        
        if (!"HOSPITAL".equals(hospital.getType())) {
            throw new BusinessException("只有医院可以创建岗位");
        }
        
        // 创建岗位
        Post post = Post.builder()
                .hospital(hospital)
                .title(request.getTitle())
                .department(request.getDepartment())
                .description(request.getDescription())
                .quota(request.getQuota())
                .duration(request.getDuration())
                .visibleSchools(convertSchoolIdsToJson(request.getVisibleSchoolIds()))
                .status("DRAFT")
                .build();
        
        Post savedPost = postRepository.save(post);
        return convertToResponse(savedPost);
    }
    
    @Override
    public PostResponse updatePost(Long postId, PostUpdateRequest request) {
        String userRole = SessionContext.getCurrentUserRole();
        Long userOrgId = SessionContext.getCurrentOrganizationId();
        
        // 只有医院管理员可以编辑岗位
        if (!"HOSPITAL_ADMIN".equals(userRole)) {
            throw new AccessDeniedException("只有医院管理员可以编辑岗位");
        }
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("岗位不存在"));
        
        // 验证岗位所属医院
        if (!post.getHospital().getId().equals(userOrgId)) {
            throw new AccessDeniedException("无权编辑其他医院的岗位");
        }
        
        // 更新岗位信息
        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getDepartment() != null) {
            post.setDepartment(request.getDepartment());
        }
        if (request.getDescription() != null) {
            post.setDescription(request.getDescription());
        }
        if (request.getQuota() != null) {
            post.setQuota(request.getQuota());
        }
        if (request.getDuration() != null) {
            post.setDuration(request.getDuration());
        }
        if (request.getVisibleSchoolIds() != null) {
            post.setVisibleSchools(convertSchoolIdsToJson(request.getVisibleSchoolIds()));
        }
        
        Post updatedPost = postRepository.save(post);
        return convertToResponse(updatedPost);
    }
    
    @Override
    public PostResponse updatePostStatus(Long postId, PostStatusUpdateRequest request) {
        String userRole = SessionContext.getCurrentUserRole();
        Long userOrgId = SessionContext.getCurrentOrganizationId();
        
        // 只有医院管理员可以上下架岗位
        if (!"HOSPITAL_ADMIN".equals(userRole)) {
            throw new AccessDeniedException("只有医院管理员可以上下架岗位");
        }
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("岗位不存在"));
        
        // 验证岗位所属医院
        if (!post.getHospital().getId().equals(userOrgId)) {
            throw new AccessDeniedException("无权操作其他医院的岗位");
        }
        
        // 验证状态值
        String status = request.getStatus();
        if (!"PUBLISHED".equals(status) && !"ARCHIVED".equals(status)) {
            throw new BusinessException("无效的岗位状态");
        }
        
        post.setStatus(status);
        Post updatedPost = postRepository.save(post);
        return convertToResponse(updatedPost);
    }
    
    @Override
    public boolean isPostQuotaFull(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("岗位不存在"));
        
        // 统计已通过医院审批的申请数量
        List<com.medical.internship.entity.Application> applications = applicationRepository.findByPostId(postId);
        long approvedCount = applications.stream()
                .filter(app -> "APPROVED".equals(app.getHospitalStatus()))
                .count();
        
        return approvedCount >= post.getQuota();
    }
    
    /**
     * 根据学校ID过滤岗位
     */
    private List<Post> filterPostsBySchool(List<Post> posts, Long schoolId) {
        return posts.stream()
                .filter(post -> isPostVisibleToSchool(post, schoolId))
                .collect(Collectors.toList());
    }
    
    /**
     * 检查岗位是否对学校可见
     */
    private boolean isPostVisibleToSchool(Post post, Long schoolId) {
        if (post.getVisibleSchools() == null || post.getVisibleSchools().isEmpty()) {
            return true; // 如果没有设置可见学校，则对所有学校可见
        }
        
        try {
            List<Long> visibleSchoolIds = objectMapper.readValue(
                    post.getVisibleSchools(),
                    new TypeReference<List<Long>>() {}
            );
            return visibleSchoolIds.contains(schoolId);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 验证用户对岗位的访问权限
     */
    private void validatePostAccess(Post post) {
        String userRole = SessionContext.getCurrentUserRole();
        Long userOrgId = SessionContext.getCurrentOrganizationId();
        
        // 系统管理员可以访问所有岗位
        if ("ADMIN".equals(userRole)) {
            return;
        }
        
        // 医院管理员和带教老师可以访问本院岗位
        if (("HOSPITAL_ADMIN".equals(userRole) || "TEACHER".equals(userRole)) 
                && post.getHospital().getId().equals(userOrgId)) {
            return;
        }
        
        // 学校管理员和学生只能访问面向本校的已发布岗位
        if (("SCHOOL_ADMIN".equals(userRole) || "STUDENT".equals(userRole)) 
                && "PUBLISHED".equals(post.getStatus())
                && isPostVisibleToSchool(post, userOrgId)) {
            return;
        }
        
        throw new AccessDeniedException("无权访问该岗位");
    }
    
    /**
     * 将Post实体转换为PostResponse
     */
    private PostResponse convertToResponse(Post post) {
        List<Long> visibleSchoolIds = new ArrayList<>();
        if (post.getVisibleSchools() != null && !post.getVisibleSchools().isEmpty()) {
            try {
                visibleSchoolIds = objectMapper.readValue(
                        post.getVisibleSchools(),
                        new TypeReference<List<Long>>() {}
                );
            } catch (Exception e) {
                // 如果解析失败，返回空列表
            }
        }
        
        // 统计申请数量
        long applicationCount = applicationRepository.countByPostId(post.getId());
        
        return PostResponse.builder()
                .id(post.getId())
                .hospital(convertOrganizationToResponse(post.getHospital()))
                .title(post.getTitle())
                .department(post.getDepartment())
                .description(post.getDescription())
                .quota(post.getQuota())
                .duration(post.getDuration())
                .visibleSchoolIds(visibleSchoolIds)
                .status(post.getStatus())
                .applicationCount((int) applicationCount)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
    
    /**
     * 将Organization实体转换为OrganizationResponse
     */
    private OrganizationResponse convertOrganizationToResponse(Organization organization) {
        return OrganizationResponse.builder()
                .id(organization.getId())
                .name(organization.getName())
                .type(organization.getType())
                .code(organization.getCode())
                .build();
    }
    
    /**
     * 将学校ID列表转换为JSON字符串
     */
    private String convertSchoolIdsToJson(List<Long> schoolIds) {
        if (schoolIds == null || schoolIds.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(schoolIds);
        } catch (Exception e) {
            throw new BusinessException("学校ID列表格式错误");
        }
    }
}
