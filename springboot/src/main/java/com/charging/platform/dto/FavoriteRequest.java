package com.charging.platform.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FavoriteRequest {
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    @NotNull(message = "充电站ID不能为空")
    private Long stationId;
}
