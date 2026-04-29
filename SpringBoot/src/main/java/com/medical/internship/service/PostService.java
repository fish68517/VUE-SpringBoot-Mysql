package com.medical.internship.service;

import com.medical.internship.dto.PostCreateRequest;
import com.medical.internship.dto.PostResponse;
import com.medical.internship.dto.PostStatusUpdateRequest;
import com.medical.internship.dto.PostUpdateRequest;

import java.util.List;

/**
 * 岗位服务接口
 */
public interface PostService {
    
    /**
     * 获取岗位列表（根据用户身份进行数据隔离）
     */
    List<PostResponse> getPostList();
    
    /**
     * 获取岗位详情
     */
    PostResponse getPostDetail(Long postId);
    
    /**
     * 创建岗位（医院管理员）
     */
    PostResponse createPost(PostCreateRequest request);
    
    /**
     * 更新岗位（医院管理员）
     */
    PostResponse updatePost(Long postId, PostUpdateRequest request);
    
    /**
     * 更新岗位状态（上下架）
     */
    PostResponse updatePostStatus(Long postId, PostStatusUpdateRequest request);
    
    /**
     * 检查岗位名额是否已满
     */
    boolean isPostQuotaFull(Long postId);
}
