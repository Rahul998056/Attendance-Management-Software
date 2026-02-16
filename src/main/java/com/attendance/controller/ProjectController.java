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

    @Autowired
    private com.attendance.service.JwtUtil jwtUtil;

    /**
     * Get all projects
     * GET /api/projects
     */
    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        List<Project> projects = projectService.getAllProjects(adminId);
        return ResponseEntity.ok(projects);
    }

    /**
     * Get project by ID
     * GET /api/projects/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                  @PathVariable Long id) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        Project project = projectService.getProjectById(adminId, id);
        return ResponseEntity.ok(project);
    }

    /**
     * Get projects by status
     * GET /api/projects/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Project>> getProjectsByStatus(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                              @PathVariable String status) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        List<Project> projects = projectService.getProjectsByStatus(adminId, status);
        return ResponseEntity.ok(projects);
    }

    /**
     * Get active projects
     * GET /api/projects/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<Project>> getActiveProjects(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        List<Project> projects = projectService.getActiveProjects(adminId);
        return ResponseEntity.ok(projects);
    }

    /**
     * Get projects by client name
     * GET /api/projects/client/{clientName}
     */
    @GetMapping("/client/{clientName}")
    public ResponseEntity<List<Project>> getProjectsByClientName(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                                  @PathVariable String clientName) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        List<Project> projects = projectService.getProjectsByClientName(adminId, clientName);
        return ResponseEntity.ok(projects);
    }

    /**
     * Search projects by client name
     * GET /api/projects/search/client?term={searchTerm}
     */
    @GetMapping("/search/client")
    public ResponseEntity<List<Project>> searchProjectsByClientName(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                                     @RequestParam String term) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        List<Project> projects = projectService.searchProjectsByClientName(adminId, term);
        return ResponseEntity.ok(projects);
    }

    /**
     * Search projects by project name
     * GET /api/projects/search/project?term={searchTerm}
     */
    @GetMapping("/search/project")
    public ResponseEntity<List<Project>> searchProjectsByProjectName(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                                      @RequestParam String term) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        List<Project> projects = projectService.searchProjectsByProjectName(adminId, term);
        return ResponseEntity.ok(projects);
    }

    /**
     * Get projects by start date range
     * GET /api/projects/date-range?startDate={start}&endDate={end}
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<Project>> getProjectsByStartDateRange(@RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        List<Project> projects = projectService.getProjectsByStartDateRange(adminId, startDate, endDate);
        return ResponseEntity.ok(projects);
    }

    /**
     * Create a new project
     * POST /api/projects
     */
    @PostMapping
    public ResponseEntity<Project> createProject(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                 @RequestBody Project project) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        Project createdProject = projectService.createProject(adminId, project);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    /**
     * Update a project
     * PUT /api/projects/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                 @PathVariable Long id, @RequestBody Project projectDetails) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        Project updatedProject = projectService.updateProject(adminId, id, projectDetails);
        return ResponseEntity.ok(updatedProject);
    }

    /**
     * Update project status
     * PATCH /api/projects/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Project> updateProjectStatus(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                       @PathVariable Long id,
                                                       @RequestParam String status) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        Project updatedProject = projectService.updateProjectStatus(adminId, id, status);
        return ResponseEntity.ok(updatedProject);
    }

    /**
     * Delete a project
     * DELETE /api/projects/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                              @PathVariable Long id) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        projectService.deleteProject(adminId, id);
        return ResponseEntity.noContent().build();
    }
}
