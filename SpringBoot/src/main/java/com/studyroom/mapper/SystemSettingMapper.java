package com.studyroom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyroom.entity.SystemSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SystemSettingMapper extends BaseMapper<SystemSetting> {

    @Select("SELECT setting_value FROM system_setting WHERE setting_key = #{key}")
    String getValueByKey(@Param("key") String key);
}
