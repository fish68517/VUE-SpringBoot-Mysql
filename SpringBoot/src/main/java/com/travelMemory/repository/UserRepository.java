package com.travelMemory.repository;

import com.travelMemory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email
     * @param email the user's email
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by username
     * @param username the user's username
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if email exists
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Check if username exists
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    boolean existsByUsername(String username);
}
