package com.admin.service;

import com.admin.entity.ExerciseData;
import com.admin.mapper.ExerciseDataMapper;
import com.admin.vo.PageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ExerciseDataService {

    @Autowired
    private ExerciseDataMapper exerciseDataMapper;

    public ExerciseData getExerciseDataById(Long id) {
        if (id == null || id <= 0) {
            return null;
        }
        return exerciseDataMapper.findById(id);
    }

    public PageResponse<ExerciseData> getExerciseDataList(int page, int pageSize) {
        if (page < 1) {
            page = 1;
        }
        if (pageSize < 1 || pageSize > 100) {
            pageSize = 10;
        }

        int offset = (page - 1) * pageSize;
        List<ExerciseData> exerciseDataList = exerciseDataMapper.findAll(offset, pageSize);
        int total = exerciseDataMapper.countAll();
        return new PageResponse<>(exerciseDataList, total, page, pageSize);
    }

    public ExerciseData dailyCheckIn(ExerciseData exerciseData) {
        if (exerciseData == null || exerciseData.getUserId() == null || exerciseData.getUserId() <= 0) {
            return null;
        }

        ExerciseData todayData = exerciseDataMapper.findTodayByUserId(exerciseData.getUserId());
        if (todayData != null) {
            return todayData;
        }

        if (exerciseData.getExerciseType() == null || exerciseData.getExerciseType().trim().isEmpty()) {
            exerciseData.setExerciseType("Daily Check-in");
        }
        if (exerciseData.getLocation() == null || exerciseData.getLocation().trim().isEmpty()) {
            exerciseData.setLocation("Campus");
        }
        if (exerciseData.getDuration() == null || exerciseData.getDuration() <= 0) {
            exerciseData.setDuration(30);
        }

        int result = exerciseDataMapper.insert(exerciseData);
        if (result <= 0 || exerciseData.getId() == null) {
            return null;
        }
        return exerciseDataMapper.findById(exerciseData.getId());
    }

    public boolean deleteExerciseData(Long id) {
        if (id == null || id <= 0) {
            return false;
        }

        ExerciseData existingData = exerciseDataMapper.findById(id);
        if (existingData == null) {
            return false;
        }

        return exerciseDataMapper.deleteById(id) > 0;
    }
}
