package com.charging.platform.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FavoriteStateVO {
    private boolean favorited;
    private Long favoriteId;
}
