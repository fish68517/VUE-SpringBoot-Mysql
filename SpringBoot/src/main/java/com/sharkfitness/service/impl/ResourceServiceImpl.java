package com.sharkfitness.service.impl;

import com.sharkfitness.dto.ResourceCreateRequest;
import com.sharkfitness.entity.FitnessResource;
import com.sharkfitness.entity.User;
import com.sharkfitness.exception.ResourceNotFoundException;
import com.sharkfitness.exception.UnauthorizedException;
import com.sharkfitness.repository.ResourceRepository;
import com.sharkfitness.repository.UserRepository;
import com.sharkfitness.service.ResourceService;
import com.sharkfitness.vo.ResourceVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of ResourceService
 */
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public ResourceVO create(ResourceCreateRequest request, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Check if user has permission to create resources (Admin or Coach)
        if (!"admin".equals(creator.getRole()) && !"coach".equals(creator.getRole())) {
            throw new UnauthorizedException("Only admins and coaches can create resources");
        }
        
        FitnessResource resource = new FitnessResource();
        resource.setTitle(request.getTitle());
        resource.setDescription(request.getDescription());
        resource.setContentType(request.getContentType());
        resource.setFileUrl(request.getFileUrl());
        resource.setContent(request.getContent());
        resource.setCreator(creator);
        
        FitnessResource saved = resourceRepository.save(resource);
        return convertToVO(saved);
    }
    
    @Override
    @Transactional
    public ResourceVO update(Long id, ResourceCreateRequest request, Long currentUserId) {
        FitnessResource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Check if user has permission to update (Admin or the creator)
        if (!"admin".equals(currentUser.getRole()) && 
            !resource.getCreator().getId().equals(currentUserId)) {
            throw new UnauthorizedException("You don't have permission to update this resource");
        }
        
        resource.setTitle(request.getTitle());
        resource.setDescription(request.getDescription());
        resource.setContentType(request.getContentType());
        resource.setFileUrl(request.getFileUrl());
        resource.setContent(request.getContent());
        
        FitnessResource updated = resourceRepository.save(resource);
        return convertToVO(updated);
    }
    
    @Override
    @Transactional
    public void delete(Long id, Long currentUserId) {
        FitnessResource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Only admin can delete resources
        if (!"admin".equals(currentUser.getRole())) {
            throw new UnauthorizedException("Only admins can delete resources");
        }
        
        resourceRepository.delete(resource);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ResourceVO> findAll(Pageable pageable) {
        return resourceRepository.findAll(pageable)
                .map(this::convertToVO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ResourceVO> findByContentType(String contentType, Pageable pageable) {
        return resourceRepository.findByContentType(contentType, pageable)
                .map(this::convertToVO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ResourceVO findById(Long id) {
        FitnessResource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        return convertToVO(resource);
    }
    
    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        FitnessResource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        resource.setViewCount(resource.getViewCount() + 1);
        resourceRepository.save(resource);
    }
    
    private ResourceVO convertToVO(FitnessResource resource) {
        ResourceVO vo = new ResourceVO();
        vo.setId(resource.getId());
        vo.setTitle(resource.getTitle());
        vo.setDescription(resource.getDescription());
        vo.setContentType(resource.getContentType());
        vo.setFileUrl(resource.getFileUrl());
        vo.setContent(resource.getContent());
        vo.setViewCount(resource.getViewCount());
        vo.setCreatedAt(resource.getCreatedAt());
        vo.setUpdatedAt(resource.getUpdatedAt());
        
        if (resource.getCreator() != null) {
            vo.setCreatorId(resource.getCreator().getId());
            vo.setCreatorName(resource.getCreator().getUsername());
            vo.setCreatorRole(resource.getCreator().getRole());
        }
        
        return vo;
    }
}
