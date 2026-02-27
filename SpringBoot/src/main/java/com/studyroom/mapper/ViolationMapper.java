package com.studyroom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyroom.entity.Violation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ViolationMapper extends BaseMapper<Violation> {

    @Select("SELECT COUNT(*) FROM violation WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);
}
