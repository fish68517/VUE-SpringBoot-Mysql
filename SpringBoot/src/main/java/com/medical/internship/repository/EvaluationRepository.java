package com.medical.internship.repository;

import com.medical.internship.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评价数据访问接口
 */
@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    
    /**
     * 根据实习记录ID查询评价列表
     */
    List<Evaluation> findByInternshipId(Long internshipId);
    
    /**
     * 根据评价人ID查询评价列表
     */
    List<Evaluation> findByEvaluatorId(Long evaluatorId);
    
    /**
     * 根据实习记录ID和评价人类型查询评价列表
     */
    List<Evaluation> findByInternshipIdAndEvaluatorType(Long internshipId, String evaluatorType);
}
