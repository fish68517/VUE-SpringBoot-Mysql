package com.health.repository;

import com.health.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 咨询数据访问层接口
 * 提供咨询相关的数据库操作方法
 */
@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    /**
     * 按用户ID查询所有咨询
     * @param userId 用户ID
     * @return 咨询列表
     */
    List<Consultation> findByUserId(Long userId);

    /**
     * 按医师ID查询所有咨询
     * @param doctorId 医师ID
     * @return 咨询列表
     */
    List<Consultation> findByDoctorId(Long doctorId);

    /**
     * 按用户ID和状态查询咨询
     * @param userId 用户ID
     * @param status 咨询状态
     * @return 咨询列表
     */
    List<Consultation> findByUserIdAndStatus(Long userId, String status);

    /**
     * 按医师ID和状态查询咨询
     * @param doctorId 医师ID
     * @param status 咨询状态
     * @return 咨询列表
     */
    List<Consultation> findByDoctorIdAndStatus(Long doctorId, String status);

    /**
     * 按状态查询所有咨询
     * @param status 咨询状态
     * @return 咨询列表
     */
    List<Consultation> findByStatus(String status);
}
