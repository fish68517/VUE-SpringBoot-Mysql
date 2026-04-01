package com.naxi.repository;

import com.naxi.entity.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {
    /**
     * 按settingKey查询系统参数
     */
    Optional<SystemSetting> findBySettingKey(String settingKey);
}
