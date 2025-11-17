package com.sharkfitness.vo;

import com.sharkfitness.entity.DietRecord;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DietRecordVO {
    private Long id;
    private Long userId;
    private String username;
    private String mealType;
    private String foodItems;
    private Integer calories;
    private LocalDate mealDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DietRecordVO fromEntity(DietRecord dietRecord) {
        DietRecordVO vo = new DietRecordVO();
        vo.setId(dietRecord.getId());
        vo.setUserId(dietRecord.getUser().getId());
        vo.setUsername(dietRecord.getUser().getUsername());
        vo.setMealType(dietRecord.getMealType());
        vo.setFoodItems(dietRecord.getFoodItems());
        vo.setCalories(dietRecord.getCalories());
        vo.setMealDate(dietRecord.getMealDate());
        vo.setCreatedAt(dietRecord.getCreatedAt());
        vo.setUpdatedAt(dietRecord.getUpdatedAt());
        return vo;
    }
}
