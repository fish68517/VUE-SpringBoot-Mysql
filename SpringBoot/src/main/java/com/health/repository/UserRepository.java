package com.health.repository;

import com.health.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问层接口
 * 提供用户相关的数据库操作方法
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 按用户名查询用户
     * @param username 用户名
     * @return 用户对象
     */
    Optional<User> findByUsername(String username);

    /**
     * 按角色查询所有用户
     * @param role 角色
     * @return 用户列表
     */
    List<User> findByRole(String role);

    /**
     * 按邮箱查询用户
     * @param email 邮箱
     * @return 用户对象
     */
    Optional<User> findByEmail(String email);

    /**
     * 按状态查询所有用户
     * @param status 账户状态
     * @return 用户列表
     */
    List<User> findByStatus(String status);

    /**
     * 按角色和状态查询用户
     * @param role 角色
     * @param status 账户状态
     * @return 用户列表
     */
    List<User> findByRoleAndStatus(String role, String status);
}
