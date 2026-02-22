package com.agricultural.repository;

import com.agricultural.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 * 用户数据访问层接口
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
     * 根据用户类型查询用户列表
     */
    List<User> findByUserType(User.UserType userType);
    
    /**
     * 根据地区查询用户列表
     */
    List<User> findByRegion(String region);
    
    /**
     * 根据用户状态查询用户列表
     */
    List<User> findByStatus(User.UserStatus status);
}
