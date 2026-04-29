package com.medical.internship.repository;

import com.medical.internship.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 岗位数据访问接口
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    /**
     * 根据医院ID查询岗位列表
     */
    List<Post> findByHospitalId(Long hospitalId);
    
    /**
     * 根据医院ID和状态查询岗位列表
     */
    List<Post> findByHospitalIdAndStatus(Long hospitalId, String status);
    
    /**
     * 根据状态查询岗位列表
     */
    List<Post> findByStatus(String status);
    
    /**
     * 查询已发布的岗位列表
     */
    List<Post> findByStatus(String status, org.springframework.data.domain.Pageable pageable);
    
    /**
     * 根据医院ID查询已发布的岗位
     */
    List<Post> findByHospitalIdAndStatus(Long hospitalId, String status, org.springframework.data.domain.Pageable pageable);
}
