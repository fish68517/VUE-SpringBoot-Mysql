package com.admin.service;

import com.admin.entity.FitnessPlan;
import com.admin.mapper.FitnessPlanMapper;
import com.admin.vo.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
public class FitnessPlanService {

    @Autowired
    private FitnessPlanMapper fitnessPlanMapper;

    /**
     * Get fitness plan by id
     * 
     * @param id fitness plan id
     * @return FitnessPlan if found, null otherwise
     */
    public FitnessPlan getFitnessPlanById(Long id) {
        if (id == null || id <= 0) {
            log.warn("获取健身计划失败: 无效的计划ID - {}", id);
            return null;
        }

        FitnessPlan fitnessPlan = fitnessPlanMapper.findById(id);
        
        if (fitnessPlan == null) {
            log.warn("获取健身计划失败: 计划不存在 - {}", id);
            return null;
        }

        return fitnessPlan;
    }

    /**
     * Get all fitness plans with pagination
     * 
     * @param page page number (1-based)
     * @param pageSize page size
     * @return PageResponse with fitness plan list
     */
    public PageResponse<FitnessPlan> getFitnessPlanList(int page, int pageSize) {
        if (page < 1) {
            page = 1;
        }
        if (pageSize < 1 || pageSize > 100) {
            pageSize = 10;
        }

        int offset = (page - 1) * pageSize;
        
        List<FitnessPlan> fitnessPlanList = fitnessPlanMapper.findAll(offset, pageSize);
        int total = fitnessPlanMapper.countAll();

        log.info("获取健身计划列表: 页码={}, 页大小={}, 总数={}", page, pageSize, total);

        return new PageResponse<>(fitnessPlanList, total, page, pageSize);
    }

    /**
     * Update fitness plan
     * 
     * @param id fitness plan id
     * @param fitnessPlan fitness plan data to update
     * @return true if update successful, false otherwise
     */
    public boolean updateFitnessPlan(Long id, FitnessPlan fitnessPlan) {
        if (id == null || id <= 0) {
            log.warn("更新健身计划失败: 无效的计划ID - {}", id);
            return false;
        }

        FitnessPlan existingPlan = fitnessPlanMapper.findById(id);
        if (existingPlan == null) {
            log.warn("更新健身计划失败: 计划不存在 - {}", id);
            return false;
        }

        FitnessPlan updatePlan = new FitnessPlan();
        updatePlan.setId(id);
        updatePlan.setGoal(fitnessPlan.getGoal());
        updatePlan.setPlanContent(fitnessPlan.getPlanContent());

        int result = fitnessPlanMapper.update(updatePlan);
        
        if (result > 0) {
            log.info("健身计划更新成功: {}", id);
            return true;
        } else {
            log.warn("健身计划更新失败: {}", id);
            return false;
        }
    }

    /**
     * Delete fitness plan by id
     * 
     * @param id fitness plan id
     * @return true if delete successful, false otherwise
     */
    public boolean deleteFitnessPlan(Long id) {
        if (id == null || id <= 0) {
            log.warn("删除健身计划失败: 无效的计划ID - {}", id);
            return false;
        }

        FitnessPlan existingPlan = fitnessPlanMapper.findById(id);
        if (existingPlan == null) {
            log.warn("删除健身计划失败: 计划不存在 - {}", id);
            return false;
        }

        int result = fitnessPlanMapper.deleteById(id);
        
        if (result > 0) {
            log.info("健身计划删除成功: {}", id);
            return true;
        } else {
            log.warn("健身计划删除失败: {}", id);
            return false;
        }
    }
}
