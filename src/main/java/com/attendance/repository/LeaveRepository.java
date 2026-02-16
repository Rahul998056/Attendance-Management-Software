package com.attendance.repository;

import com.attendance.entity.AdminCredential;
import com.attendance.entity.Employee;
import com.attendance.entity.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {

    /**
     * Find all leave requests for an employee
     * 
     * @param employee The employee
     * @return List of leave requests
     */
    List<Leave> findByEmployee(Employee employee);

    /**
     * Find leave requests by status
     * 
     * @param status The leave status (PENDING, APPROVED, REJECTED)
     * @return List of leave requests
     */
    List<Leave> findByStatus(String status);

    /**
     * Find leave requests by employee and status
     * 
     * @param employee The employee
     * @param status   The status
     * @return List of leave requests
     */
    List<Leave> findByEmployeeAndStatus(Employee employee, String status);

    /**
     * Find leave requests by leave type
     * 
     * @param leaveType The leave type (SICK, CASUAL, ANNUAL, UNPAID)
     * @return List of leave requests
     */
    List<Leave> findByLeaveType(String leaveType);

    /**
     * Find leave requests by employee and leave type
     * 
     * @param employee  The employee
     * @param leaveType The leave type
     * @return List of leave requests
     */
    List<Leave> findByEmployeeAndLeaveType(Employee employee, String leaveType);

    /**
     * Find leave requests within a date range
     * 
     * @param startDate Start of range
     * @param endDate   End of range
     * @return List of leave requests
     */
    @Query("SELECT l FROM Leave l WHERE l.startDate <= :endDate AND l.endDate >= :startDate")
    List<Leave> findLeavesInDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find approved leaves for an employee in a date range
     * 
     * @param employee  The employee
     * @param startDate Start of range
     * @param endDate   End of range
     * @return List of approved leaves
     */
    @Query("SELECT l FROM Leave l WHERE l.employee = :employee AND l.status = 'APPROVED' " +
            "AND l.startDate <= :endDate AND l.endDate >= :startDate")
    List<Leave> findApprovedLeavesForEmployeeInDateRange(
            @Param("employee") Employee employee,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Find all pending leave requests
     * 
     * @return List of pending leave requests
     */
    default List<Leave> findPendingLeaves() {
        return findByStatus("PENDING");
    }

    /**
     * Count leave days for employee in a year
     * 
     * @param employee The employee
     * @param year     The year
     * @return Number of leave days
     */
    @Query("SELECT COALESCE(SUM(DATEDIFF(l.endDate, l.startDate) + 1), 0) FROM Leave l " +
            "WHERE l.employee = :employee AND l.status = 'APPROVED' AND YEAR(l.startDate) = :year")
    long countApprovedLeaveDaysForYear(@Param("employee") Employee employee, @Param("year") int year);
    
    // Admin-filtered queries
    List<Leave> findByAdmin(AdminCredential admin);
    
    List<Leave> findByAdminAndStatus(AdminCredential admin, String status);
    
    Optional<Leave> findByAdminAndId(AdminCredential admin, Long id);

        // Additional admin-scoped helpers
        List<Leave> findByAdminAndEmployee(AdminCredential admin, Employee employee);

        List<Leave> findByAdminAndEmployeeAndStatus(AdminCredential admin, Employee employee, String status);

        List<Leave> findByAdminAndLeaveType(AdminCredential admin, String leaveType);

        default List<Leave> findPendingLeavesForAdmin(AdminCredential admin) {
                return findByAdminAndStatus(admin, "PENDING");
        }
}
