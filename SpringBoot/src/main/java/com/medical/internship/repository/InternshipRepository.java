package com.medical.internship.repository;

import com.medical.internship.entity.Internship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 实习记录数据访问接口
 */
@Repository
public interface InternshipRepository extends JpaRepository<Internship, Long> {
    
    /**
     * 根据学生ID查询实习记录列表
     */
    List<Internship> findByStudentId(Long studentId);
    
    /**
     * 根据带教老师ID查询实习记录列表
     */
    List<Internship> findByTeacherId(Long teacherId);
    
    /**
     * 根据岗位ID查询实习记录列表
     */
    List<Internship> findByPostId(Long postId);
    
    /**
     * 根据状态查询实习记录列表
     */
    List<Internship> findByStatus(String status);
    
    /**
     * 根据申请ID查询实习记录
     */
    Optional<Internship> findByApplicationId(Long applicationId);

    // 检查该申请是否已经创建过实习记录
    boolean existsByApplicationId(Long applicationId);
}
