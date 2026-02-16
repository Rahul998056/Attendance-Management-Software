package com.attendance.repository;

import com.attendance.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find role by role name
     * 
     * @param roleName The role name (e.g., "ADMIN", "HR", "EMPLOYEE")
     * @return Optional containing the role if found
     */
    Optional<Role> findByRoleName(String roleName);

    /**
     * Check if role exists by name
     * 
     * @param roleName The role name to check
     * @return true if role exists, false otherwise
     */
    boolean existsByRoleName(String roleName);
}
