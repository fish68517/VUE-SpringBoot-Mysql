// PointsMallItemDTO.java
package com.shenyang.musicfestival.dto;
import lombok.Builder;
import lombok.Data;
@Data @Builder
public class PointsMallItemDTO {
    private Long id;
    private String name;
    private String description;
    private Integer pointsRequired;
    private Integer stock;
    private String image; // 只取第一张图片给前端
}