package com.sharkfitness.service.impl;

import com.sharkfitness.dto.DietRecordRequest;
import com.sharkfitness.entity.DietRecord;
import com.sharkfitness.entity.User;
import com.sharkfitness.exception.BusinessException;
import com.sharkfitness.repository.DietRecordRepository;
import com.sharkfitness.service.DietRecordService;
import com.sharkfitness.vo.DietRecordVO;
import com.sharkfitness.vo.DietSummaryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DietRecordServiceImpl implements DietRecordService {

    @Autowired
    private DietRecordRepository dietRecordRepository;
    
    @Autowired
    private com.sharkfitness.repository.CoachStudentRepository coachStudentRepository;
    
    @Autowired
    private com.sharkfitness.repository.UserRepository userRepository;

    @Override
    @Transactional
    public DietRecordVO create(User user, DietRecordRequest request) {
        DietRecord dietRecord = new DietRecord();
        dietRecord.setUser(user);
        dietRecord.setMealType(request.getMealType());
        dietRecord.setFoodItems(request.getFoodItems());
        dietRecord.setCalories(request.getCalories());
        dietRecord.setMealDate(request.getMealDate());

        dietRecord = dietRecordRepository.save(dietRecord);
        return DietRecordVO.fromEntity(dietRecord);
    }

    @Override
    @Transactional
    public DietRecordVO update(Long id, User user, DietRecordRequest request) {
        DietRecord dietRecord = dietRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Diet record not found"));

        if (!dietRecord.getUser().getId().equals(user.getId())) {
            throw new BusinessException(403, "You can only update your own diet records");
        }

        dietRecord.setMealType(request.getMealType());
        dietRecord.setFoodItems(request.getFoodItems());
        dietRecord.setCalories(request.getCalories());
        dietRecord.setMealDate(request.getMealDate());

        dietRecord = dietRecordRepository.save(dietRecord);
        return DietRecordVO.fromEntity(dietRecord);
    }

    @Override
    @Transactional
    public void delete(Long id, User user) {
        DietRecord dietRecord = dietRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Diet record not found"));

        if (!dietRecord.getUser().getId().equals(user.getId())) {
            throw new BusinessException(403, "You can only delete your own diet records");
        }

        dietRecordRepository.delete(dietRecord);
    }

    @Override
    public List<DietRecordVO> findByUserAndDate(User user, LocalDate startDate, LocalDate endDate) {
        List<DietRecord> records;
        
        if (startDate != null && endDate != null) {
            records = dietRecordRepository.findByUserAndMealDateBetweenOrderByMealDateDesc(user, startDate, endDate);
        } else {
            records = dietRecordRepository.findByUserOrderByMealDateDesc(user);
        }

        return records.stream()
                .map(DietRecordVO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public DietSummaryVO calculateDailySummary(User user, LocalDate date) {
        List<DietRecord> records = dietRecordRepository.findByUserAndMealDateOrderByCreatedAtAsc(user, date);

        int totalCalories = 0;
        int breakfastCalories = 0;
        int lunchCalories = 0;
        int dinnerCalories = 0;
        int snackCalories = 0;

        for (DietRecord record : records) {
            int calories = record.getCalories() != null ? record.getCalories() : 0;
            totalCalories += calories;

            String mealType = record.getMealType().toLowerCase();
            switch (mealType) {
                case "breakfast":
                    breakfastCalories += calories;
                    break;
                case "lunch":
                    lunchCalories += calories;
                    break;
                case "dinner":
                    dinnerCalories += calories;
                    break;
                case "snack":
                    snackCalories += calories;
                    break;
            }
        }

        return new DietSummaryVO(
                date,
                totalCalories,
                breakfastCalories,
                lunchCalories,
                dinnerCalories,
                snackCalories,
                records.size()
        );
    }
    
    @Override
    public List<DietRecordVO> getStudentDietRecords(Long coachId, Long studentId, LocalDate startDate, LocalDate endDate) {
        // Validate that coach-student relationship exists
        if (!coachStudentRepository.existsByCoachIdAndStudentId(coachId, studentId)) {
            throw new com.sharkfitness.exception.BusinessException(403, "You do not have access to this student's data");
        }
        
        // Get the student user
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new com.sharkfitness.exception.BusinessException(404, "Student not found"));
        
        // Return the student's diet records
        return findByUserAndDate(student, startDate, endDate);
    }
}
