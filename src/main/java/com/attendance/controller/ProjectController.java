package com.attendance.controller;

import com.attendance.entity.Project;
import com.attendance.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    /**
     * Get all projects
     * GET /api/projects
     */
    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    /**
     * Get project by ID
     * GET /api/projects/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        Project project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    /**
     * Get projects by status
     * GET /api/projects/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Project>> getProjectsByStatus(@PathVariable String status) {
        List<Project> projects = projectService.getProjectsByStatus(status);
        return ResponseEntity.ok(projects);
    }

    /**
     * Get active projects
     * GET /api/projects/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<Project>> getActiveProjects() {
        List<Project> projects = projectService.getActiveProjects();
        return ResponseEntity.ok(projects);
    }

    /**
     * Get projects by client name
     * GET /api/projects/client/{clientName}
     */
    @GetMapping("/client/{clientName}")
    public ResponseEntity<List<Project>> getProjectsByClientName(@PathVariable String clientName) {
        List<Project> projects = projectService.getProjectsByClientName(clientName);
        return ResponseEntity.ok(projects);
    }

    /**
     * Search projects by client name
     * GET /api/projects/search/client?term={searchTerm}
     */
    @GetMapping("/search/client")
    public ResponseEntity<List<Project>> searchProjectsByClientName(@RequestParam String term) {
        List<Project> projects = projectService.searchProjectsByClientName(term);
        return ResponseEntity.ok(projects);
    }

    /**
     * Search projects by project name
     * GET /api/projects/search/project?term={searchTerm}
     */
    @GetMapping("/search/project")
    public ResponseEntity<List<Project>> searchProjectsByProjectName(@RequestParam String term) {
        List<Project> projects = projectService.searchProjectsByProjectName(term);
        return ResponseEntity.ok(projects);
    }

    /**
     * Get projects by start date range
     * GET /api/projects/date-range?startDate={start}&endDate={end}
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<Project>> getProjectsByStartDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Project> projects = projectService.getProjectsByStartDateRange(startDate, endDate);
        return ResponseEntity.ok(projects);
    }

    /**
     * Create a new project
     * POST /api/projects
     */
    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        Project createdProject = projectService.createProject(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    /**
     * Update a project
     * PUT /api/projects/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project projectDetails) {
        Project updatedProject = projectService.updateProject(id, projectDetails);
        return ResponseEntity.ok(updatedProject);
    }

    /**
     * Update project status
     * PATCH /api/projects/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Project> updateProjectStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Project updatedProject = projectService.updateProjectStatus(id, status);
        return ResponseEntity.ok(updatedProject);
    }

    /**
     * Delete a project
     * DELETE /api/projects/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
