package com.medical.internship.repository;

import com.medical.internship.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查询用户
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查询用户
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 根据组织ID和角色查询用户列表
     */
    List<User> findByOrganizationIdAndRole(Long organizationId, String role);
    
    /**
     * 根据组织ID查询用户列表
     */
    List<User> findByOrganizationId(Long organizationId);
    
    /**
     * 根据状态查询用户列表
     */
    List<User> findByStatus(String status);
}
