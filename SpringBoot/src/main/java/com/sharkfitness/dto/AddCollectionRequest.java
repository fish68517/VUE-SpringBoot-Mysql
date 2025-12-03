package com.sharkfitness.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddCollectionRequest {
    @NotNull(message = "资源ID不能为空")
    private Long resourceId;
}
