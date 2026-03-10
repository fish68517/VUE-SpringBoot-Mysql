// CheckinTaskDTO.java
package com.shenyang.musicfestival.dto;
import lombok.Builder;
import lombok.Data;
@Data @Builder
public class CheckinTaskDTO {
    private Long id;
    private String name;
    private String description;
    private Integer points;
    private String status; // 动态计算：ongoing 或 completed
}