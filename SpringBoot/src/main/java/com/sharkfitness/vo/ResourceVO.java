package com.sharkfitness.vo;

import com.sharkfitness.entity.FitnessResource;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Value object for FitnessResource responses
 */
@Data
public class ResourceVO {
    
    private Long id;
    private String title;
    private String description;
    private String contentType;
    private String fileUrl;
    private String content;
    private Long creatorId;
    private String creatorName;
    private String creatorRole;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ResourceVO fromEntity(FitnessResource resource) {
        ResourceVO vo = new ResourceVO();
        vo.setId(resource.getId());
        vo.setTitle(resource.getTitle());
        vo.setDescription(resource.getDescription());
        vo.setContentType(resource.getContentType());
        vo.setFileUrl(resource.getFileUrl());
        vo.setContent(resource.getContent());
        if (resource.getCreator() != null) {
            vo.setCreatorId(resource.getCreator().getId());
            vo.setCreatorName(resource.getCreator().getUsername());
            vo.setCreatorRole(resource.getCreator().getRole());
        }
        vo.setViewCount(resource.getViewCount());
        vo.setCreatedAt(resource.getCreatedAt());
        vo.setUpdatedAt(resource.getUpdatedAt());
        return vo;
    }
}
