package com.admin.controller;

import com.admin.entity.ExerciseData;
import com.admin.service.ExerciseDataService;
import com.admin.vo.ApiResponse;
import com.admin.vo.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/exercise-data")
public class ExerciseDataController {

    @Autowired
    private ExerciseDataService exerciseDataService;

    @PostMapping("/checkin")
    public ApiResponse<ExerciseData> dailyCheckIn(@RequestBody ExerciseData exerciseData) {
        ExerciseData checkedInData = exerciseDataService.dailyCheckIn(exerciseData);
        if (checkedInData == null) {
            return ApiResponse.error(400, "Check-in failed");
        }
        return ApiResponse.success("Check-in success", checkedInData);
    }

    @GetMapping
    public ApiResponse<PageResponse<ExerciseData>> getExerciseDataList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        if (page < 1 || pageSize < 1 || pageSize > 100) {
            return ApiResponse.error(400, "Invalid paging params");
        }
        return ApiResponse.success("Get exercise data success",
                exerciseDataService.getExerciseDataList(page, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<ExerciseData> getExerciseDataById(@PathVariable Long id) {
        ExerciseData exerciseData = exerciseDataService.getExerciseDataById(id);
        if (exerciseData == null) {
            return ApiResponse.error(404, "Exercise data not found");
        }
        return ApiResponse.success("Get exercise data success", exerciseData);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Object> deleteExerciseData(@PathVariable Long id) {
        boolean success = exerciseDataService.deleteExerciseData(id);
        if (!success) {
            return ApiResponse.error(404, "Exercise data not found");
        }
        return ApiResponse.success("Delete exercise data success", null);
    }
}
