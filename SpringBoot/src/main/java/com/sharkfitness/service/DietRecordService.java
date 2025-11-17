package com.sharkfitness.service;

import com.sharkfitness.dto.DietRecordRequest;
import com.sharkfitness.entity.User;
import com.sharkfitness.vo.DietRecordVO;
import com.sharkfitness.vo.DietSummaryVO;

import java.time.LocalDate;
import java.util.List;

public interface DietRecordService {
    
    DietRecordVO create(User user, DietRecordRequest request);
    
    DietRecordVO update(Long id, User user, DietRecordRequest request);
    
    void delete(Long id, User user);
    
    List<DietRecordVO> findByUserAndDate(User user, LocalDate startDate, LocalDate endDate);
    
    DietSummaryVO calculateDailySummary(User user, LocalDate date);
    
    /**
     * Get diet records for a student (coach access)
     * @param coachId Coach's user ID
     * @param studentId Student's user ID
     * @param startDate Start date for filtering (optional)
     * @param endDate End date for filtering (optional)
     * @return List of diet records
     */
    List<DietRecordVO> getStudentDietRecords(Long coachId, Long studentId, LocalDate startDate, LocalDate endDate);
}
