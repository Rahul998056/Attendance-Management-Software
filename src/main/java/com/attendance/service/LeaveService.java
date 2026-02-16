package com.attendance.service;

import com.attendance.entity.Employee;
import com.attendance.entity.Leave;
import com.attendance.repository.EmployeeRepository;
import com.attendance.repository.LeaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LeaveService {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Get all leave requests
     * 
     * @return List of all leaves
     */
    public List<Leave> getAllLeaves() {
        return leaveRepository.findAll();
    }

    /**
     * Get leave by ID
     * 
     * @param id Leave ID
     * @return Leave entity
     */
    public Leave getLeaveById(Long id) {
        return leaveRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Leave not found with id: " + id));
    }

    /**
     * Get all leaves for an employee
     * 
     * @param employeeId Employee ID
     * @return List of leaves
     */
    public List<Leave> getLeavesByEmployeeId(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Employee not found: " + employeeId));
        return leaveRepository.findByEmployee(employee);
    }

    /**
     * Get leaves by status
     * 
     * @param status Leave status (PENDING, APPROVED, REJECTED)
     * @return List of leaves
     */
    public List<Leave> getLeavesByStatus(String status) {
        return leaveRepository.findByStatus(status);
    }

    /**
     * Get pending leave requests
     * 
     * @return List of pending leaves
     */
    public List<Leave> getPendingLeaves() {
        return leaveRepository.findPendingLeaves();
    }

    /**
     * Get leaves by employee and status
     * 
     * @param employeeId Employee ID
     * @param status     Leave status
     * @return List of leaves
     */
    public List<Leave> getLeavesByEmployeeAndStatus(Long employeeId, String status) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Employee not found: " + employeeId));
        return leaveRepository.findByEmployeeAndStatus(employee, status);
    }

    /**
     * Get leaves by type
     * 
     * @param leaveType Leave type (SICK, CASUAL, ANNUAL, UNPAID)
     * @return List of leaves
     */
    public List<Leave> getLeavesByType(String leaveType) {
        return leaveRepository.findByLeaveType(leaveType);
    }

    /**
     * Get leaves in a date range
     * 
     * @param startDate Start date
     * @param endDate   End date
     * @return List of leaves
     */
    public List<Leave> getLeavesInDateRange(LocalDate startDate, LocalDate endDate) {
        return leaveRepository.findLeavesInDateRange(startDate, endDate);
    }

    /**
     * Create a leave request
     * 
     * @param leave Leave entity
     * @return Created leave
     */
    public Leave createLeave(Leave leave) {
        // Validate employee exists
        if (leave.getEmployee() == null || leave.getEmployee().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Employee is required");
        }

        Employee employee = employeeRepository.findById(leave.getEmployee().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Employee not found"));

        leave.setEmployee(employee);

        // Validate dates
        if (leave.getStartDate() == null || leave.getEndDate() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Start date and end date are required");
        }

        if (leave.getEndDate().isBefore(leave.getStartDate())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "End date cannot be before start date");
        }

        // Check for overlapping leaves
        List<Leave> overlappingLeaves = leaveRepository
                .findApprovedLeavesForEmployeeInDateRange(
                        employee, leave.getStartDate(), leave.getEndDate());

        if (!overlappingLeaves.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Leave dates overlap with existing approved leave");
        }

        // Set default status if not provided
        if (leave.getStatus() == null || leave.getStatus().isEmpty()) {
            leave.setStatus("PENDING");
        }

        return leaveRepository.save(leave);
    }

    /**
     * Update a leave request
     * 
     * @param id           Leave ID
     * @param leaveDetails Updated leave details
     * @return Updated leave
     */
    public Leave updateLeave(Long id, Leave leaveDetails) {
        Leave leave = getLeaveById(id);

        if (leaveDetails.getLeaveType() != null) {
            leave.setLeaveType(leaveDetails.getLeaveType());
        }

        if (leaveDetails.getStartDate() != null) {
            leave.setStartDate(leaveDetails.getStartDate());
        }

        if (leaveDetails.getEndDate() != null) {
            leave.setEndDate(leaveDetails.getEndDate());
        }

        // Validate dates
        if (leave.getStartDate() != null && leave.getEndDate() != null) {
            if (leave.getEndDate().isBefore(leave.getStartDate())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "End date cannot be before start date");
            }
        }

        if (leaveDetails.getReason() != null) {
            leave.setReason(leaveDetails.getReason());
        }

        if (leaveDetails.getStatus() != null) {
            leave.setStatus(leaveDetails.getStatus());
        }

        return leaveRepository.save(leave);
    }

    /**
     * Approve a leave request
     * 
     * @param id Leave ID
     * @return Updated leave
     */
    public Leave approveLeave(Long id) {
        Leave leave = getLeaveById(id);

        if (!"PENDING".equals(leave.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Only pending leaves can be approved");
        }

        // Check for overlapping approved leaves
        List<Leave> overlappingLeaves = leaveRepository
                .findApprovedLeavesForEmployeeInDateRange(
                        leave.getEmployee(), leave.getStartDate(), leave.getEndDate());

        if (!overlappingLeaves.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Leave dates overlap with existing approved leave");
        }

        leave.setStatus("APPROVED");
        return leaveRepository.save(leave);
    }

    /**
     * Reject a leave request
     * 
     * @param id Leave ID
     * @return Updated leave
     */
    public Leave rejectLeave(Long id) {
        Leave leave = getLeaveById(id);

        if (!"PENDING".equals(leave.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Only pending leaves can be rejected");
        }

        leave.setStatus("REJECTED");
        return leaveRepository.save(leave);
    }

    /**
     * Delete a leave request
     * 
     * @param id Leave ID
     */
    public void deleteLeave(Long id) {
        Leave leave = getLeaveById(id);
        leaveRepository.delete(leave);
    }

    /**
     * Calculate number of leave days
     * 
     * @param startDate Start date
     * @param endDate   End date
     * @return Number of days
     */
    public long calculateLeaveDays(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    /**
     * Get total approved leave days for employee in a year
     * 
     * @param employeeId Employee ID
     * @param year       Year
     * @return Total leave days
     */
    public long getTotalLeaveDaysForYear(Long employeeId, int year) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Employee not found: " + employeeId));
        return leaveRepository.countApprovedLeaveDaysForYear(employee, year);
    }

    /**
     * Get remaining leave balance for employee
     * 
     * @param employeeId       Employee ID
     * @param year             Year
     * @param annualLeaveQuota Annual leave quota
     * @return Remaining leave days
     */
    public long getRemainingLeaveBalance(Long employeeId, int year, long annualLeaveQuota) {
        long usedDays = getTotalLeaveDaysForYear(employeeId, year);
        return annualLeaveQuota - usedDays;
    }
}
