package com.studyroom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyroom.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Update("UPDATE user SET monthly_study_time = monthly_study_time + #{minutes}, " +
            "total_study_time = total_study_time + #{minutes} WHERE id = #{userId}")
    int addStudyTime(@Param("userId") Long userId, @Param("minutes") Integer minutes);

    @Update("UPDATE user SET monthly_study_time = 0")
    int resetMonthlyStudyTime();
}
