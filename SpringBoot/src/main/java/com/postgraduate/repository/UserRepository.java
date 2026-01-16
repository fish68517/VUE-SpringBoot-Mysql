package com.postgraduate.repository;

import com.postgraduate.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity providing database access operations.
 * Supports user authentication, profile management, and admin user management operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by username.
     *
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if a username already exists.
     *
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Find all users with a specific status, excluding soft-deleted records.
     *
     * @param status the user status to filter by
     * @param pageable pagination information
     * @return Page of users with the specified status
     */
    Page<User> findByStatusAndDeletedFalse(User.UserStatus status, Pageable pageable);

    /**
     * Find users by username containing the search keyword (case-insensitive), excluding soft-deleted records.
     *
     * @param keyword the search keyword
     * @param pageable pagination information
     * @return Page of users matching the keyword
     */
    Page<User> findByUsernameContainingIgnoreCaseAndDeletedFalse(String keyword, Pageable pageable);

    /**
     * Find users by status and username containing the search keyword (case-insensitive), excluding soft-deleted records.
     *
     * @param status the user status to filter by
     * @param keyword the search keyword
     * @param pageable pagination information
     * @return Page of users matching both status and keyword
     */
    Page<User> findByStatusAndUsernameContainingIgnoreCaseAndDeletedFalse(User.UserStatus status, String keyword, Pageable pageable);

    /**
     * Find all non-deleted users with pagination.
     *
     * @param pageable pagination information
     * @return Page of all non-deleted users
     */
    Page<User> findByDeletedFalse(Pageable pageable);

}
