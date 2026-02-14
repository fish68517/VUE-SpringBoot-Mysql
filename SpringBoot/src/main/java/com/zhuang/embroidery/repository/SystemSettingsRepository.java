package com.zhuang.embroidery.repository;

import com.zhuang.embroidery.entity.SystemSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 系统设置数据访问层接口
 */
@Repository
public interface SystemSettingsRepository extends JpaRepository<SystemSettings, Long> {

    /**
     * 根据网站名称查询系统设置
     *
     * @param siteName 网站名称
     * @return 系统设置
     */
    SystemSettings findBySiteName(String siteName);
}
