package com.attendance.service;

import com.attendance.entity.Project;
import com.attendance.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private com.attendance.repository.AdminCredentialRepository adminCredentialRepository;

    /**
     * Get all projects
     * 
     * @return List of all projects
     */
    public List<Project> getAllProjects(Long adminId) {
        com.attendance.entity.AdminCredential admin = adminCredentialRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));
        return projectRepository.findByAdmin(admin);
    }

    /**
     * Get project by ID
     * 
     * @param id Project ID
     * @return Project entity
     */
        public Project getProjectById(Long adminId, Long id) {
        com.attendance.entity.AdminCredential admin = adminCredentialRepository.findById(adminId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));
        return projectRepository.findByAdminAndId(admin, id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found with id: " + id));
        }

    /**
     * Get projects by status
     * 
     * @param status Project status (ACTIVE, COMPLETED, ON_HOLD)
     * @return List of projects
     */
    public List<Project> getProjectsByStatus(Long adminId, String status) {
        com.attendance.entity.AdminCredential admin = adminCredentialRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));
        return projectRepository.findByAdminAndStatus(admin, status);
    }

    /**
     * Get active projects
     * 
     * @return List of active projects
     */
    public List<Project> getActiveProjects(Long adminId) {
        com.attendance.entity.AdminCredential admin = adminCredentialRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));
        return projectRepository.findByAdminAndStatus(admin, "ACTIVE");
    }

    /**
     * Get projects by client name
     * 
     * @param clientName Client name
     * @return List of projects
     */
    public List<Project> getProjectsByClientName(Long adminId, String clientName) {
        com.attendance.entity.AdminCredential admin = adminCredentialRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));
        return projectRepository.findByAdminAndStatus(admin, clientName); // fallback to admin filtered client search if needed
    }

    /**
     * Search projects by client name
     * 
     * @param searchTerm Search term
     * @return List of matching projects
     */
    public List<Project> searchProjectsByClientName(Long adminId, String searchTerm) {
        com.attendance.entity.AdminCredential admin = adminCredentialRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));
        // Use admin-scoped list and then filter in-memory for simplicity
        return projectRepository.findByAdmin(admin).stream()
                .filter(p -> p.getClientName() != null && p.getClientName().toLowerCase().contains(searchTerm.toLowerCase()))
                .toList();
    }

    /**
     * Search projects by project name
     * 
     * @param searchTerm Search term
     * @return List of matching projects
     */
    public List<Project> searchProjectsByProjectName(Long adminId, String searchTerm) {
        com.attendance.entity.AdminCredential admin = adminCredentialRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));
        return projectRepository.findByAdmin(admin).stream()
                .filter(p -> p.getProjectName() != null && p.getProjectName().toLowerCase().contains(searchTerm.toLowerCase()))
                .toList();
    }

    /**
     * Get projects starting in a date range
     * 
     * @param startDate Start of range
     * @param endDate   End of range
     * @return List of projects
     */
    public List<Project> getProjectsByStartDateRange(Long adminId, LocalDate startDate, LocalDate endDate) {
        com.attendance.entity.AdminCredential admin = adminCredentialRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));
        return projectRepository.findByAdmin(admin).stream()
                .filter(p -> p.getStartDate() != null && !p.getStartDate().isBefore(startDate) && !p.getStartDate().isAfter(endDate))
                .toList();
    }

    /**
     * Create a new project
     * 
     * @param project Project entity
     * @return Created project
     */
    public Project createProject(Long adminId, Project project) {
        // Validate dates
        if (project.getStartDate() != null && project.getEndDate() != null) {
            if (project.getEndDate().isBefore(project.getStartDate())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "End date cannot be before start date");
            }
        }

        // Set default status if not provided
        if (project.getStatus() == null || project.getStatus().isEmpty()) {
            project.setStatus("ACTIVE");
        }

        com.attendance.entity.AdminCredential admin = adminCredentialRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));
        project.setAdmin(admin);

        return projectRepository.save(project);
    }

    /**
     * Update an existing project
     * 
     * @param id             Project ID
     * @param projectDetails Updated project details
     * @return Updated project
     */
    public Project updateProject(Long adminId, Long id, Project projectDetails) {
        Project project = getProjectById(adminId, id);

        if (projectDetails.getProjectName() != null) {
            project.setProjectName(projectDetails.getProjectName());
        }

        if (projectDetails.getClientName() != null) {
            project.setClientName(projectDetails.getClientName());
        }

        if (projectDetails.getStartDate() != null) {
            project.setStartDate(projectDetails.getStartDate());
        }

        if (projectDetails.getEndDate() != null) {
            project.setEndDate(projectDetails.getEndDate());
        }

        // Validate dates
        if (project.getStartDate() != null && project.getEndDate() != null) {
            if (project.getEndDate().isBefore(project.getStartDate())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "End date cannot be before start date");
            }
        }

        if (projectDetails.getStatus() != null) {
            project.setStatus(projectDetails.getStatus());
        }

        return projectRepository.save(project);
    }

    /**
     * Update project status
     * 
     * @param id     Project ID
     * @param status New status
     * @return Updated project
     */
    public Project updateProjectStatus(Long adminId, Long id, String status) {
        Project project = getProjectById(adminId, id);
        project.setStatus(status);
        return projectRepository.save(project);
    }

    /**
     * Delete a project
     * 
     * @param id Project ID
     */
    public void deleteProject(Long adminId, Long id) {
        Project project = getProjectById(adminId, id);
        projectRepository.delete(project);
    }
}
