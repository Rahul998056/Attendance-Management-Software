package com.attendance.repository;

import com.attendance.entity.AdminCredential;
import com.attendance.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByUser_Id(Long userId);
    
    // Admin-filtered queries
    List<Employee> findByAdmin(AdminCredential admin);
    
    List<Employee> findByAdminAndDepartment(AdminCredential admin, String department);
    
    Optional<Employee> findByAdminAndId(AdminCredential admin, Long id);
    
    Optional<Employee> findByAdminAndUser_Id(AdminCredential admin, Long userId);
}
