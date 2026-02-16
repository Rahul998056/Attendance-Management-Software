package com.attendance.repository;

import com.attendance.entity.Role;
import com.attendance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username
     * 
     * @param username The username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     * 
     * @param email The email to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by username or email (for login)
     * 
     * @param username The username
     * @param email    The email
     * @return Optional containing the user if found
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * Find all users by role
     * 
     * @param role The role to filter by
     * @return List of users with the specified role
     */
    List<User> findByRole(Role role);

    /**
     * Find all active users
     * 
     * @param isActive The active status
     * @return List of active/inactive users
     */
    List<User> findByIsActive(Boolean isActive);

    /**
     * Check if username exists
     * 
     * @param username The username to check
     * @return true if username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     * 
     * @param email The email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
