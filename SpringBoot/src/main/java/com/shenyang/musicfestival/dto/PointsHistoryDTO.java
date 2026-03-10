// PointsHistoryDTO.java
package com.shenyang.musicfestival.dto;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
@Data @Builder
public class PointsHistoryDTO {
    private Long id;
    private Integer changeAmount;
    private String description;
    private LocalDateTime createdAt;
}