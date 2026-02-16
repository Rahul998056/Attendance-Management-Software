package com.attendance.repository;

import com.attendance.entity.Employee;
import com.attendance.entity.EmployeeProject;
import com.attendance.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeProjectRepository extends JpaRepository<EmployeeProject, Long> {

    /**
     * Find all project assignments for an employee
     * 
     * @param employee The employee
     * @return List of employee-project assignments
     */
    List<EmployeeProject> findByEmployee(Employee employee);

    /**
     * Find all employee assignments for a project
     * 
     * @param project The project
     * @return List of employee-project assignments
     */
    List<EmployeeProject> findByProject(Project project);

    /**
     * Find specific employee-project assignment
     * 
     * @param employee The employee
     * @param project  The project
     * @return Optional containing the assignment if found
     */
    Optional<EmployeeProject> findByEmployeeAndProject(Employee employee, Project project);

    /**
     * Check if employee is assigned to project
     * 
     * @param employee The employee
     * @param project  The project
     * @return true if assigned, false otherwise
     */
    boolean existsByEmployeeAndProject(Employee employee, Project project);

    /**
     * Find all employees assigned to a specific project
     * 
     * @param projectId The project ID
     * @return List of employees
     */
    @Query("SELECT ep.employee FROM EmployeeProject ep WHERE ep.project.id = :projectId")
    List<Employee> findEmployeesByProjectId(@Param("projectId") Long projectId);

    /**
     * Find all projects assigned to a specific employee
     * 
     * @param employeeId The employee ID
     * @return List of projects
     */
    @Query("SELECT ep.project FROM EmployeeProject ep WHERE ep.employee.id = :employeeId")
    List<Project> findProjectsByEmployeeId(@Param("employeeId") Long employeeId);

    /**
     * Count employees assigned to a project
     * 
     * @param project The project
     * @return Number of employees assigned
     */
    long countByProject(Project project);

    /**
     * Count projects assigned to an employee
     * 
     * @param employee The employee
     * @return Number of projects assigned
     */
    long countByEmployee(Employee employee);
}
