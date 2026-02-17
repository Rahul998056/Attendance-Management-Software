package com.attendance.repository;

import com.attendance.entity.AdminCredential;
import com.attendance.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * Find projects by status
     * 
     * @param status The project status (ACTIVE, COMPLETED, ON_HOLD)
     * @return List of projects with the specified status
     */
    List<Project> findByStatus(String status);

    /**
     * Find projects by client name
     * 
     * @param clientName The client name
     * @return List of projects for the client
     */
    List<Project> findByClientName(String clientName);

    /**
     * Find projects by client name containing (case-insensitive search)
     * 
     * @param clientName The client name to search for
     * @return List of matching projects
     */
    List<Project> findByClientNameContainingIgnoreCase(String clientName);

    /**
     * Find projects by project name containing (case-insensitive search)
     * 
     * @param projectName The project name to search for
     * @return List of matching projects
     */
    List<Project> findByProjectNameContainingIgnoreCase(String projectName);

    /**
     * Find projects starting between dates
     * 
     * @param startDate Start of date range
     * @param endDate   End of date range
     * @return List of projects
     */
    List<Project> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find active projects (status = ACTIVE)
     * 
     * @return List of active projects
     */
    default List<Project> findActiveProjects() {
        return findByStatus("ACTIVE");
    }
    
    // Admin-filtered queries
    List<Project> findByAdmin(AdminCredential admin);
    
    List<Project> findByAdminAndStatus(AdminCredential admin, String status);
    
    Optional<Project> findByAdminAndId(AdminCredential admin, Long id);
}
