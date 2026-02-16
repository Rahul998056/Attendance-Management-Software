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

    /**
     * Get all projects
     * 
     * @return List of all projects
     */
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    /**
     * Get project by ID
     * 
     * @param id Project ID
     * @return Project entity
     */
    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project not found with id: " + id));
    }

    /**
     * Get projects by status
     * 
     * @param status Project status (ACTIVE, COMPLETED, ON_HOLD)
     * @return List of projects
     */
    public List<Project> getProjectsByStatus(String status) {
        return projectRepository.findByStatus(status);
    }

    /**
     * Get active projects
     * 
     * @return List of active projects
     */
    public List<Project> getActiveProjects() {
        return projectRepository.findActiveProjects();
    }

    /**
     * Get projects by client name
     * 
     * @param clientName Client name
     * @return List of projects
     */
    public List<Project> getProjectsByClientName(String clientName) {
        return projectRepository.findByClientName(clientName);
    }

    /**
     * Search projects by client name
     * 
     * @param searchTerm Search term
     * @return List of matching projects
     */
    public List<Project> searchProjectsByClientName(String searchTerm) {
        return projectRepository.findByClientNameContainingIgnoreCase(searchTerm);
    }

    /**
     * Search projects by project name
     * 
     * @param searchTerm Search term
     * @return List of matching projects
     */
    public List<Project> searchProjectsByProjectName(String searchTerm) {
        return projectRepository.findByProjectNameContainingIgnoreCase(searchTerm);
    }

    /**
     * Get projects starting in a date range
     * 
     * @param startDate Start of range
     * @param endDate   End of range
     * @return List of projects
     */
    public List<Project> getProjectsByStartDateRange(LocalDate startDate, LocalDate endDate) {
        return projectRepository.findByStartDateBetween(startDate, endDate);
    }

    /**
     * Create a new project
     * 
     * @param project Project entity
     * @return Created project
     */
    public Project createProject(Project project) {
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

        return projectRepository.save(project);
    }

    /**
     * Update an existing project
     * 
     * @param id             Project ID
     * @param projectDetails Updated project details
     * @return Updated project
     */
    public Project updateProject(Long id, Project projectDetails) {
        Project project = getProjectById(id);

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
    public Project updateProjectStatus(Long id, String status) {
        Project project = getProjectById(id);
        project.setStatus(status);
        return projectRepository.save(project);
    }

    /**
     * Delete a project
     * 
     * @param id Project ID
     */
    public void deleteProject(Long id) {
        Project project = getProjectById(id);
        projectRepository.delete(project);
    }
}
