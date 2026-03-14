package com.admin.controller;

import com.admin.entity.FitnessPlan;
import com.admin.service.FitnessPlanService;
import com.admin.vo.ApiResponse;
import com.admin.vo.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/admin/fitness-plans")
public class FitnessPlanController {

    @Autowired
    private FitnessPlanService fitnessPlanService;

    /**
     * Get fitness plan list with pagination
     * 
     * @param page page number (default 1)
     * @param pageSize page size (default 10)
     * @return ApiResponse with PageResponse containing fitness plan list
     */
    @GetMapping
    public ApiResponse<PageResponse<FitnessPlan>> getFitnessPlanList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        log.info("获取健身计划列表请求: page={}, pageSize={}", page, pageSize);
        
        if (page < 1) {
            return ApiResponse.error(400, "页码必须大于0");
        }
        
        if (pageSize < 1 || pageSize > 100) {
            return ApiResponse.error(400, "页大小必须在1-100之间");
        }

        PageResponse<FitnessPlan> pageResponse = fitnessPlanService.getFitnessPlanList(page, pageSize);
        return ApiResponse.success("获取健身计划列表成功", pageResponse);
    }

    /**
     * Get fitness plan details by id
     * 
     * @param id fitness plan id
     * @return ApiResponse with FitnessPlan if found, error response otherwise
     */
    @GetMapping("/{id}")
    public ApiResponse<FitnessPlan> getFitnessPlanById(@PathVariable Long id) {
        log.info("获取健身计划详情请求: id={}", id);
        
        if (id == null || id <= 0) {
            return ApiResponse.error(400, "计划ID无效");
        }

        FitnessPlan fitnessPlan = fitnessPlanService.getFitnessPlanById(id);
        
        if (fitnessPlan == null) {
            return ApiResponse.error(404, "健身计划不存在");
        }

        return ApiResponse.success("获取健身计划详情成功", fitnessPlan);
    }

    /**
     * Update fitness plan
     * 
     * @param id fitness plan id
     * @param fitnessPlan fitness plan data to update
     * @return ApiResponse with success or error message
     */
    @PutMapping("/{id}")
    public ApiResponse<Object> updateFitnessPlan(@PathVariable Long id, @RequestBody FitnessPlan fitnessPlan) {
        log.info("更新健身计划请求: id={}", id);
        
        if (id == null || id <= 0) {
            return ApiResponse.error(400, "计划ID无效");
        }

        if (fitnessPlan == null) {
            return ApiResponse.error(400, "计划数据不能为空");
        }

        boolean success = fitnessPlanService.updateFitnessPlan(id, fitnessPlan);
        
        if (!success) {
            return ApiResponse.error(404, "健身计划不存在或更新失败");
        }

        return ApiResponse.success("健身计划更新成功", null);
    }

    /**
     * Delete fitness plan by id
     * 
     * @param id fitness plan id
     * @return ApiResponse with success or error message
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Object> deleteFitnessPlan(@PathVariable Long id) {
        log.info("删除健身计划请求: id={}", id);
        
        if (id == null || id <= 0) {
            return ApiResponse.error(400, "计划ID无效");
        }

        boolean success = fitnessPlanService.deleteFitnessPlan(id);
        
        if (!success) {
            return ApiResponse.error(404, "健身计划不存在或删除失败");
        }

        return ApiResponse.success("健身计划删除成功", null);
    }
}
