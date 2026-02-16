package com.attendance.repository;

import com.attendance.entity.AdminCredential;
import com.attendance.entity.Employee;
import com.attendance.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    /**
     * Find all payroll records for an employee
     * 
     * @param employee The employee
     * @return List of payroll records
     */
    List<Payroll> findByEmployee(Employee employee);

    /**
     * Find payroll by employee, month, and year
     * 
     * @param employee The employee
     * @param month    The month name
     * @param year     The year
     * @return Optional containing the payroll record if found
     */
    Optional<Payroll> findByEmployeeAndMonthAndYear(Employee employee, String month, Integer year);

    /**
     * Find all payroll records for a specific month and year
     * 
     * @param month The month name
     * @param year  The year
     * @return List of payroll records
     */
    List<Payroll> findByMonthAndYear(String month, Integer year);

    /**
     * Find all payroll records for a specific year
     * 
     * @param year The year
     * @return List of payroll records
     */
    List<Payroll> findByYear(Integer year);

    /**
     * Check if payroll exists for employee in specific month/year
     * 
     * @param employee The employee
     * @param month    The month
     * @param year     The year
     * @return true if exists, false otherwise
     */
    boolean existsByEmployeeAndMonthAndYear(Employee employee, String month, Integer year);

    /**
     * Calculate total payroll for a month
     * 
     * @param month The month
     * @param year  The year
     * @return Total net salary for the month
     */
    @Query("SELECT COALESCE(SUM(p.netSalary), 0) FROM Payroll p WHERE p.month = :month AND p.year = :year")
    BigDecimal calculateTotalPayrollForMonth(@Param("month") String month, @Param("year") Integer year);

    /**
     * Find payroll records ordered by net salary descending
     * 
     * @param month The month
     * @param year  The year
     * @return List of payroll records ordered by salary
     */
    List<Payroll> findByMonthAndYearOrderByNetSalaryDesc(String month, Integer year);
    
    // Admin-filtered queries
    List<Payroll> findByAdmin(AdminCredential admin);
    
    List<Payroll> findByAdminAndMonthAndYear(AdminCredential admin, String month, Integer year);
    
    Optional<Payroll> findByAdminAndId(AdminCredential admin, Long id);
}
