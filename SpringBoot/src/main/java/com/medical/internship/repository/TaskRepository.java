package com.medical.internship.repository;

import com.medical.internship.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 任务数据访问接口
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    /**
     * 根据实习记录ID查询任务列表
     */
    List<Task> findByInternshipId(Long internshipId);
}
