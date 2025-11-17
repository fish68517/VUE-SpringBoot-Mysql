package com.sharkfitness.service.impl;

import com.sharkfitness.entity.Collection;
import com.sharkfitness.entity.FitnessResource;
import com.sharkfitness.entity.User;
import com.sharkfitness.exception.BusinessException;
import com.sharkfitness.exception.ResourceNotFoundException;
import com.sharkfitness.repository.CollectionRepository;
import com.sharkfitness.repository.ResourceRepository;
import com.sharkfitness.repository.UserRepository;
import com.sharkfitness.service.CollectionService;
import com.sharkfitness.vo.ResourceVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of CollectionService
 */
@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {
    
    private final CollectionRepository collectionRepository;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    
    @Override
    @Transactional
    public void addToCollection(Long userId, Long resourceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        FitnessResource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        
        // Check if already in collection
        if (collectionRepository.existsByUserAndResource(user, resource)) {
            throw new BusinessException(400, "Resource already in collection");
        }
        
        Collection collection = new Collection();
        collection.setUser(user);
        collection.setResource(resource);
        
        collectionRepository.save(collection);
    }
    
    @Override
    @Transactional
    public void removeFromCollection(Long userId, Long resourceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        FitnessResource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        
        Collection collection = collectionRepository.findByUserAndResource(user, resource)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));
        
        collectionRepository.delete(collection);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ResourceVO> getUserCollections(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        List<Collection> collections = collectionRepository.findByUser(user);
        
        return collections.stream()
                .map(collection -> convertToVO(collection.getResource()))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isInCollection(Long userId, Long resourceId) {
        return collectionRepository.existsByUserIdAndResourceId(userId, resourceId);
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
