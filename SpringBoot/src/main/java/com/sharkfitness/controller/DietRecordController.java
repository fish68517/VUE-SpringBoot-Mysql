package com.sharkfitness.controller;

import com.sharkfitness.dto.DietRecordRequest;
import com.sharkfitness.entity.User;
import com.sharkfitness.service.DietRecordService;
import com.sharkfitness.vo.ApiResponse;
import com.sharkfitness.vo.DietRecordVO;
import com.sharkfitness.vo.DietSummaryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/diet-records")
public class DietRecordController {

    @Autowired
    private DietRecordService dietRecordService;

    @PostMapping
    public ApiResponse<DietRecordVO> create(
            @RequestAttribute("currentUser") User currentUser,
            @Valid @RequestBody DietRecordRequest request) {
        DietRecordVO dietRecord = dietRecordService.create(currentUser, request);
        return ApiResponse.success(dietRecord);
    }

    @PutMapping("/{id}")
    public ApiResponse<DietRecordVO> update(
            @PathVariable Long id,
            @RequestAttribute("currentUser") User currentUser,
            @Valid @RequestBody DietRecordRequest request) {
        DietRecordVO dietRecord = dietRecordService.update(id, currentUser, request);
        return ApiResponse.success(dietRecord);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @PathVariable Long id,
            @RequestAttribute("currentUser") User currentUser) {
        dietRecordService.delete(id, currentUser);
        return ApiResponse.success(null);
    }

    @GetMapping
    public ApiResponse<List<DietRecordVO>> list(
            @RequestAttribute("currentUser") User currentUser,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<DietRecordVO> records = dietRecordService.findByUserAndDate(currentUser, startDate, endDate);
        return ApiResponse.success(records);
    }

    @GetMapping("/summary")
    public ApiResponse<DietSummaryVO> getDailySummary(
            @RequestAttribute("currentUser") User currentUser,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DietSummaryVO summary = dietRecordService.calculateDailySummary(currentUser, date);
        return ApiResponse.success(summary);
    }
}
