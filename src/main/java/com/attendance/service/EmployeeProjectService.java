package com.attendance.service;

import com.attendance.entity.Employee;
import com.attendance.entity.EmployeeProject;
import com.attendance.entity.Project;
import com.attendance.repository.EmployeeProjectRepository;
import com.attendance.repository.EmployeeRepository;
import com.attendance.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmployeeProjectService {

    @Autowired
    private EmployeeProjectRepository employeeProjectRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ProjectRepository projectRepository;

    /**
     * Get all employee-project assignments
     * 
     * @return List of all assignments
     */
    public List<EmployeeProject> getAllAssignments() {
        return employeeProjectRepository.findAll();
    }

    /**
     * Get assignment by ID
     * 
     * @param id Assignment ID
     * @return EmployeeProject entity
     */
    public EmployeeProject getAssignmentById(Long id) {
        return employeeProjectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Assignment not found with id: " + id));
    }

    /**
     * Get all projects for an employee
     * 
     * @param employeeId Employee ID
     * @return List of projects
     */
    public List<Project> getProjectsByEmployeeId(Long employeeId) {
        return employeeProjectRepository.findProjectsByEmployeeId(employeeId);
    }

    /**
     * Get all employees assigned to a project
     * 
     * @param projectId Project ID
     * @return List of employees
     */
    public List<Employee> getEmployeesByProjectId(Long projectId) {
        return employeeProjectRepository.findEmployeesByProjectId(projectId);
    }

    /**
     * Get all assignments for an employee
     * 
     * @param employeeId Employee ID
     * @return List of assignments
     */
    public List<EmployeeProject> getAssignmentsByEmployeeId(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Employee not found: " + employeeId));
        return employeeProjectRepository.findByEmployee(employee);
    }

    /**
     * Get all assignments for a project
     * 
     * @param projectId Project ID
     * @return List of assignments
     */
    public List<EmployeeProject> getAssignmentsByProjectId(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project not found: " + projectId));
        return employeeProjectRepository.findByProject(project);
    }

    /**
     * Assign an employee to a project
     * 
     * @param employeeId Employee ID
     * @param projectId  Project ID
     * @return Created assignment
     */
    public EmployeeProject assignEmployeeToProject(Long employeeId, Long projectId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Employee not found: " + employeeId));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project not found: " + projectId));

        // Check if already assigned
        if (employeeProjectRepository.existsByEmployeeAndProject(employee, project)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Employee already assigned to this project");
        }

        EmployeeProject assignment = new EmployeeProject();
        assignment.setEmployee(employee);
        assignment.setProject(project);
        assignment.setAssignedDate(LocalDate.now());

        return employeeProjectRepository.save(assignment);
    }

    /**
     * Create an assignment manually
     * 
     * @param assignment EmployeeProject entity
     * @return Created assignment
     */
    public EmployeeProject createAssignment(EmployeeProject assignment) {
        // Validate employee exists
        if (assignment.getEmployee() == null || assignment.getEmployee().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Employee is required");
        }

        // Validate project exists
        if (assignment.getProject() == null || assignment.getProject().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Project is required");
        }

        Employee employee = employeeRepository.findById(assignment.getEmployee().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Employee not found"));

        Project project = projectRepository.findById(assignment.getProject().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project not found"));

        // Check if already assigned
        if (employeeProjectRepository.existsByEmployeeAndProject(employee, project)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Employee already assigned to this project");
        }

        assignment.setEmployee(employee);
        assignment.setProject(project);

        // Set assigned date if not provided
        if (assignment.getAssignedDate() == null) {
            assignment.setAssignedDate(LocalDate.now());
        }

        return employeeProjectRepository.save(assignment);
    }

    /**
     * Update an assignment
     * 
     * @param id                Assignment ID
     * @param assignmentDetails Updated assignment details
     * @return Updated assignment
     */
    public EmployeeProject updateAssignment(Long id, EmployeeProject assignmentDetails) {
        EmployeeProject assignment = getAssignmentById(id);

        if (assignmentDetails.getAssignedDate() != null) {
            assignment.setAssignedDate(assignmentDetails.getAssignedDate());
        }

        return employeeProjectRepository.save(assignment);
    }

    /**
     * Remove employee from project
     * 
     * @param employeeId Employee ID
     * @param projectId  Project ID
     */
    public void removeEmployeeFromProject(Long employeeId, Long projectId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Employee not found: " + employeeId));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project not found: " + projectId));

        EmployeeProject assignment = employeeProjectRepository.findByEmployeeAndProject(employee, project)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Assignment not found"));

        employeeProjectRepository.delete(assignment);
    }

    /**
     * Delete an assignment
     * 
     * @param id Assignment ID
     */
    public void deleteAssignment(Long id) {
        EmployeeProject assignment = getAssignmentById(id);
        employeeProjectRepository.delete(assignment);
    }

    /**
     * Get employee count for a project
     * 
     * @param projectId Project ID
     * @return Number of employees assigned
     */
    public long getEmployeeCountForProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project not found: " + projectId));
        return employeeProjectRepository.countByProject(project);
    }

    /**
     * Get project count for an employee
     * 
     * @param employeeId Employee ID
     * @return Number of projects assigned
     */
    public long getProjectCountForEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Employee not found: " + employeeId));
        return employeeProjectRepository.countByEmployee(employee);
    }
}
