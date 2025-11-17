package com.sharkfitness.repository;

import com.sharkfitness.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by username
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Check if username exists
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    boolean existsByUsername(String username);
    
    /**
     * Find users by role
     * @param role the role to filter by
     * @return list of users with the specified role
     */
    List<User> findByRole(String role);
    
    /**
     * Find users by username containing (case-insensitive search)
     * @param username the username pattern to search for
     * @return list of matching users
     */
    List<User> findByUsernameContainingIgnoreCase(String username);
    
    /**
     * Find users by role and username containing
     * @param role the role to filter by
     * @param username the username pattern to search for
     * @return list of matching users
     */
    List<User> findByRoleAndUsernameContainingIgnoreCase(String role, String username);
    
    /**
     * Count users by role
     * @param role the role to count
     * @return number of users with the specified role
     */
    Long countByRole(String role);
    
    /**
     * Find users by role and username or intro containing the search query (case-insensitive)
     * @param role the role to filter by
     * @param username the username pattern to search for
     * @param intro the intro pattern to search for
     * @return list of matching users
     */
    List<User> findByRoleAndUsernameContainingIgnoreCaseOrRoleAndIntroContainingIgnoreCase(
            String role1, String username, String role2, String intro);
}
