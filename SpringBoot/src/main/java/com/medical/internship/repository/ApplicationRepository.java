package com.medical.internship.repository;

import com.medical.internship.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 申请数据访问接口
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    
    /**
     * 根据学生ID查询申请列表
     */
    List<Application> findByStudentId(Long studentId);
    
    /**
     * 根据岗位ID查询申请列表
     */
    List<Application> findByPostId(Long postId);
    
    /**
     * 根据学校审批状态查询申请列表
     */
    List<Application> findBySchoolStatus(String schoolStatus);
    
    /**
     * 根据医院审批状态查询申请列表
     */
    List<Application> findByHospitalStatus(String hospitalStatus);
    
    /**
     * 根据学生ID和岗位ID查询申请
     */
    List<Application> findByStudentIdAndPostId(Long studentId, Long postId);
    
    /**
     * 统计岗位的申请数量
     */
    long countByPostId(Long postId);
    
    /**
     * 统计岗位的已批准申请数量
     */
    @Query("SELECT COUNT(a) FROM Application a WHERE a.post.id = :postId AND a.hospitalStatus = 'APPROVED'")
    long countApprovedByPostId(@Param("postId") Long postId);
}
