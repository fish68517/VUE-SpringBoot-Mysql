package com.xingluo.petshop.repository;

import com.xingluo.petshop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
    
    /**
     * 搜索用户（按用户名、昵称、邮箱、手机号）
     */
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.nickname LIKE %:keyword% OR u.email LIKE %:keyword% OR u.phone LIKE %:keyword%")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 统计时间范围内新增用户数
     */
    Long countByCreateTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

}
