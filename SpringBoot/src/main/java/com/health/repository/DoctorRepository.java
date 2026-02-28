package com.health.repository;

import com.health.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * 医师数据访问层接口
 * 提供医师相关的数据库操作方法
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    /**
     * 按用户ID查询医师
     * @param userId 用户ID
     * @return 医师对象
     */
    Optional<Doctor> findByUserId(Long userId);

    /**
     * 按执业证号查询医师
     * @param licenseNumber 执业证号
     * @return 医师对象
     */
    Optional<Doctor> findByLicenseNumber(String licenseNumber);

    /**
     * 按专科查询所有医师
     * @param specialization 专科
     * @return 医师列表
     */
    List<Doctor> findBySpecialization(String specialization);

    /**
     * 按医院查询所有医师
     * @param hospital 医院
     * @return 医师列表
     */
    List<Doctor> findByHospital(String hospital);

    /**
     * 按认证状态查询医师
     * @param verified 是否认证
     * @return 医师列表
     */
    List<Doctor> findByVerified(Boolean verified);
}
