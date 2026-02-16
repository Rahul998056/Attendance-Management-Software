package com.attendance.service;

import com.attendance.entity.Role;
import com.attendance.entity.User;
import com.attendance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    /**
     * Get all users
     * 
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get user by ID
     * 
     * @param id User ID
     * @return User entity
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with id: " + id));
    }

    /**
     * Get user by username
     * 
     * @param username Username
     * @return User entity
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with username: " + username));
    }

    /**
     * Get user by email
     * 
     * @param email Email address
     * @return User entity
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with email: " + email));
    }

    /**
     * Get users by role
     * 
     * @param roleId Role ID
     * @return List of users with the specified role
     */
    public List<User> getUsersByRole(Long roleId) {
        Role role = roleService.getRoleById(roleId);
        return userRepository.findByRole(role);
    }

    /**
     * Get active users
     * 
     * @return List of active users
     */
    public List<User> getActiveUsers() {
        return userRepository.findByIsActive(true);
    }

    /**
     * Create a new user
     * 
     * @param user User entity
     * @return Created user
     */
    public User createUser(User user) {
        // Validate username uniqueness
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Username already exists: " + user.getUsername());
        }

        // Validate email uniqueness
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email already exists: " + user.getEmail());
        }

        // TODO: Hash password before saving (implement with Spring Security)
        // user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    /**
     * Update an existing user
     * 
     * @param id          User ID
     * @param userDetails Updated user details
     * @return Updated user
     */
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);

        // Check username uniqueness (excluding current user)
        if (!user.getUsername().equals(userDetails.getUsername()) &&
                userRepository.existsByUsername(userDetails.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Username already exists: " + userDetails.getUsername());
        }

        // Check email uniqueness (excluding current user)
        if (!user.getEmail().equals(userDetails.getEmail()) &&
                userRepository.existsByEmail(userDetails.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email already exists: " + userDetails.getEmail());
        }

        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());

        // Only update password if provided
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            // TODO: Hash password (implement with Spring Security)
            user.setPassword(userDetails.getPassword());
        }

        if (userDetails.getRole() != null) {
            user.setRole(userDetails.getRole());
        }

        if (userDetails.getIsActive() != null) {
            user.setIsActive(userDetails.getIsActive());
        }

        return userRepository.save(user);
    }

    /**
     * Activate or deactivate a user
     * 
     * @param id       User ID
     * @param isActive Active status
     * @return Updated user
     */
    public User setUserActiveStatus(Long id, boolean isActive) {
        User user = getUserById(id);
        user.setIsActive(isActive);
        return userRepository.save(user);
    }

    /**
     * Delete a user
     * 
     * @param id User ID
     */
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    /**
     * Authenticate user (basic implementation)
     * TODO: Implement proper authentication with Spring Security
     * 
     * @param usernameOrEmail Username or email
     * @param password        Password
     * @return User if authenticated, null otherwise
     */
    public User authenticate(String usernameOrEmail, String password) {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElse(null);

        if (user != null && user.getPassword().equals(password) && user.getIsActive()) {
            return user;
        }

        return null;
    }
}
