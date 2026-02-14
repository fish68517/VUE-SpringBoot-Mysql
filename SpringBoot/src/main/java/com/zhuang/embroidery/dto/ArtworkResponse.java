package com.zhuang.embroidery.dto;

import com.zhuang.embroidery.entity.Artwork;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 作品响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtworkResponse {

    /**
     * 作品ID
     */
    private Long id;

    /**
     * 作品名称
     */
    private String title;

    /**
     * 作品描述
     */
    private String description;

    /**
     * 分类
     */
    private String category;

    /**
     * 作品图片URL
     */
    private String imageUrl;

    /**
     * 创作者名称
     */
    private String creator;

    /**
     * 刺绣技法
     */
    private String technique;

    /**
     * 状态
     */
    private String status;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 收藏次数
     */
    private Integer collectCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 从 Artwork 实体转换为 ArtworkResponse
     */
    public static ArtworkResponse fromArtwork(Artwork artwork) {
        return ArtworkResponse.builder()
                .id(artwork.getId())
                .title(artwork.getTitle())
                .description(artwork.getDescription())
                .category(artwork.getCategory())
                .imageUrl(artwork.getImageUrl())
                .creator(artwork.getCreator())
                .technique(artwork.getTechnique())
                .status(artwork.getStatus())
                .viewCount(artwork.getViewCount())
                .collectCount(artwork.getCollectCount())
                .createdAt(artwork.getCreatedAt())
                .updatedAt(artwork.getUpdatedAt())
                .build();
    }

}
