package com.shenyang.musicfestival.repository;

import com.shenyang.musicfestival.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhone(String phone);

    Optional<User> findByIdNumber(String idNumber);

    boolean existsByPhone(String phone);

    // 在 UserRepository 中新增：根据手机号或昵称模糊查询并分页
    org.springframework.data.domain.Page<User> findByPhoneContainingOrNicknameContaining(
            String phone, String nickname, org.springframework.data.domain.Pageable pageable);

    boolean existsByIdNumber(String idNumber);

}
