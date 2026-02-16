package com.attendance.service;

import com.attendance.entity.Role;
import com.attendance.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private com.attendance.repository.UserRepository userRepository;

    /**
     * Get all roles
     * 
     * @return List of all roles
     */
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    /**
     * Get role by ID
     * 
     * @param id Role ID
     * @return Role entity
     */
    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Role not found with id: " + id));
    }

    /**
     * Get role by name
     * 
     * @param roleName Role name (ADMIN, HR, EMPLOYEE)
     * @return Role entity
     */
    public Role getRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Role not found: " + roleName));
    }

    /**
     * Create a new role
     * 
     * @param role Role entity
     * @return Created role
     */
    public Role createRole(Role role) {
        if (roleRepository.existsByRoleName(role.getRoleName())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Role already exists: " + role.getRoleName());
        }
        return roleRepository.save(role);
    }

    /**
     * Update an existing role
     * 
     * @param id          Role ID
     * @param roleDetails Updated role details
     * @return Updated role
     */
    public Role updateRole(Long id, Role roleDetails) {
        Role role = getRoleById(id);

        // Check if new role name already exists (excluding current role)
        if (!role.getRoleName().equals(roleDetails.getRoleName()) &&
                roleRepository.existsByRoleName(roleDetails.getRoleName())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Role name already exists: " + roleDetails.getRoleName());
        }

        role.setRoleName(roleDetails.getRoleName());
        return roleRepository.save(role);
    }

    /**
     * Delete a role
     * 
     * @param id Role ID
     */
    public void deleteRole(Long id) {
        Role role = getRoleById(id);
        // Prevent deletion when users still reference this role
        java.util.List<com.attendance.entity.User> usersWithRole = userRepository.findByRole(role);
        if (usersWithRole != null && !usersWithRole.isEmpty()) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT,
                    "Cannot delete role: there are users assigned to this role");
        }
        roleRepository.delete(role);
    }

    /**
     * Initialize default roles if they don't exist
     */
    public void initializeDefaultRoles() {
        String[] defaultRoles = { "ADMIN", "HR", "EMPLOYEE" };

        for (String roleName : defaultRoles) {
            if (!roleRepository.existsByRoleName(roleName)) {
                Role role = new Role(roleName);
                roleRepository.save(role);
            }
        }
    }
}
