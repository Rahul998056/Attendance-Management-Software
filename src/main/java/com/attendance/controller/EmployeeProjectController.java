package com.attendance.controller;

import com.attendance.entity.Employee;
import com.attendance.entity.EmployeeProject;
import com.attendance.entity.Project;
import com.attendance.service.EmployeeProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employee-projects")
@CrossOrigin(origins = "*")
public class EmployeeProjectController {

    @Autowired
    private EmployeeProjectService employeeProjectService;

    /**
     * Get all assignments
     * GET /api/employee-projects
     */
    @GetMapping
    public ResponseEntity<List<EmployeeProject>> getAllAssignments() {
        List<EmployeeProject> assignments = employeeProjectService.getAllAssignments();
        return ResponseEntity.ok(assignments);
    }

    /**
     * Get assignment by ID
     * GET /api/employee-projects/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeProject> getAssignmentById(@PathVariable Long id) {
        EmployeeProject assignment = employeeProjectService.getAssignmentById(id);
        return ResponseEntity.ok(assignment);
    }

    /**
     * Get projects for an employee
     * GET /api/employee-projects/employee/{employeeId}/projects
     */
    @GetMapping("/employee/{employeeId}/projects")
    public ResponseEntity<List<Project>> getProjectsByEmployeeId(@PathVariable Long employeeId) {
        List<Project> projects = employeeProjectService.getProjectsByEmployeeId(employeeId);
        return ResponseEntity.ok(projects);
    }

    /**
     * Get employees for a project
     * GET /api/employee-projects/project/{projectId}/employees
     */
    @GetMapping("/project/{projectId}/employees")
    public ResponseEntity<List<Employee>> getEmployeesByProjectId(@PathVariable Long projectId) {
        List<Employee> employees = employeeProjectService.getEmployeesByProjectId(projectId);
        return ResponseEntity.ok(employees);
    }

    /**
     * Get assignments for an employee
     * GET /api/employee-projects/employee/{employeeId}
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EmployeeProject>> getAssignmentsByEmployeeId(@PathVariable Long employeeId) {
        List<EmployeeProject> assignments = employeeProjectService.getAssignmentsByEmployeeId(employeeId);
        return ResponseEntity.ok(assignments);
    }

    /**
     * Get assignments for a project
     * GET /api/employee-projects/project/{projectId}
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<EmployeeProject>> getAssignmentsByProjectId(@PathVariable Long projectId) {
        List<EmployeeProject> assignments = employeeProjectService.getAssignmentsByProjectId(projectId);
        return ResponseEntity.ok(assignments);
    }

    /**
     * Assign employee to project
     * POST /api/employee-projects/assign
     */
    @PostMapping("/assign")
    public ResponseEntity<EmployeeProject> assignEmployeeToProject(
            @RequestParam Long employeeId,
            @RequestParam Long projectId) {
        EmployeeProject assignment = employeeProjectService.assignEmployeeToProject(employeeId, projectId);
        return ResponseEntity.status(HttpStatus.CREATED).body(assignment);
    }

    /**
     * Create an assignment
     * POST /api/employee-projects
     */
    @PostMapping
    public ResponseEntity<EmployeeProject> createAssignment(@RequestBody EmployeeProject assignment) {
        EmployeeProject createdAssignment = employeeProjectService.createAssignment(assignment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAssignment);
    }

    /**
     * Update an assignment
     * PUT /api/employee-projects/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeProject> updateAssignment(
            @PathVariable Long id,
            @RequestBody EmployeeProject assignmentDetails) {
        EmployeeProject updatedAssignment = employeeProjectService.updateAssignment(id, assignmentDetails);
        return ResponseEntity.ok(updatedAssignment);
    }

    /**
     * Remove employee from project
     * DELETE /api/employee-projects/remove
     */
    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeEmployeeFromProject(
            @RequestParam Long employeeId,
            @RequestParam Long projectId) {
        employeeProjectService.removeEmployeeFromProject(employeeId, projectId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Delete an assignment
     * DELETE /api/employee-projects/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long id) {
        employeeProjectService.deleteAssignment(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get employee count for project
     * GET /api/employee-projects/project/{projectId}/count
     */
    @GetMapping("/project/{projectId}/count")
    public ResponseEntity<Map<String, Object>> getEmployeeCountForProject(@PathVariable Long projectId) {
        long count = employeeProjectService.getEmployeeCountForProject(projectId);
        Map<String, Object> response = new HashMap<>();
        response.put("projectId", projectId);
        response.put("employeeCount", count);
        return ResponseEntity.ok(response);
    }

    /**
     * Get project count for employee
     * GET /api/employee-projects/employee/{employeeId}/count
     */
    @GetMapping("/employee/{employeeId}/count")
    public ResponseEntity<Map<String, Object>> getProjectCountForEmployee(@PathVariable Long employeeId) {
        long count = employeeProjectService.getProjectCountForEmployee(employeeId);
        Map<String, Object> response = new HashMap<>();
        response.put("employeeId", employeeId);
        response.put("projectCount", count);
        return ResponseEntity.ok(response);
    }
}
