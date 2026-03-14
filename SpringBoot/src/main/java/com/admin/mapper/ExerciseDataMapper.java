package com.admin.mapper;

import com.admin.entity.ExerciseData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExerciseDataMapper {

    ExerciseData findById(@Param("id") Long id);

    List<ExerciseData> findAll(@Param("offset") int offset, @Param("limit") int limit);

    int countAll();

    ExerciseData findTodayByUserId(@Param("userId") Long userId);

    int insert(ExerciseData exerciseData);

    int update(ExerciseData exerciseData);

    int deleteById(@Param("id") Long id);
}
