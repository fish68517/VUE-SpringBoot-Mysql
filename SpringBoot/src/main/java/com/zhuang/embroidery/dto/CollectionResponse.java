package com.zhuang.embroidery.dto;

import com.zhuang.embroidery.entity.Artwork;
import com.zhuang.embroidery.entity.Collection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 收藏响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectionResponse {

    /**
     * 收藏ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 作品ID
     */
    private Long artworkId;

    private ArtworkResponse artworkResponse;

    /**
     * 收藏时间
     */
    private LocalDateTime collectedAt;

    /**
     * 从 Collection 实体转换为 CollectionResponse
     */
    public static CollectionResponse fromCollection(Collection collection) {
        return CollectionResponse.builder()
                .id(collection.getId())
                .userId(collection.getUserId())
                .artworkId(collection.getArtworkId())
                .collectedAt(collection.getCollectedAt())
                .build();
    }

}
