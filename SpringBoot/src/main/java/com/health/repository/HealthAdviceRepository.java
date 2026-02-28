package com.health.repository;

import com.health.entity.HealthAdvice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 健康建议数据访问层接口
 * 提供健康建议相关的数据库操作方法
 */
@Repository
public interface HealthAdviceRepository extends JpaRepository<HealthAdvice, Long> {

    /**
     * 按医师ID查询所有健康建议
     * @param doctorId 医师ID
     * @return 健康建议列表
     */
    List<HealthAdvice> findByDoctorId(Long doctorId);

    /**
     * 按患者ID查询所有健康建议
     * @param userId 患者ID
     * @return 健康建议列表
     */
    List<HealthAdvice> findByUserId(Long userId);

    /**
     * 按医师ID和患者ID查询健康建议
     * @param doctorId 医师ID
     * @param userId 患者ID
     * @return 健康建议列表
     */
    List<HealthAdvice> findByDoctorIdAndUserId(Long doctorId, Long userId);
}
