package com.admin.mapper;

import com.admin.entity.Diary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiaryMapper {

    int insert(Diary diary);

    List<Diary> findByUserId(@Param("userId") Long userId);
}
