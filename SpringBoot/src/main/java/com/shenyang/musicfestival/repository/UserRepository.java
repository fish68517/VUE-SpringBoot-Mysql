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

    boolean existsByIdNumber(String idNumber);

}
