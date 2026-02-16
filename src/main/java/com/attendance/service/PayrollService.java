package com.attendance.service;

import com.attendance.entity.Attendance;
import com.attendance.entity.Employee;
import com.attendance.entity.Payroll;
import com.attendance.repository.AttendanceRepository;
import com.attendance.repository.EmployeeRepository;
import com.attendance.repository.PayrollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.attendance.entity.Leave;
import com.attendance.repository.LeaveRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class PayrollService {

    @Autowired
    private PayrollRepository payrollRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private LeaveRepository leaveRepository;

    /**
     * Get all payroll records
     * 
     * @return List of all payroll records
     */
    public List<Payroll> getAllPayrolls() {
        return payrollRepository.findAll();
    }

    /**
     * Get payroll by ID
     * 
     * @param id Payroll ID
     * @return Payroll entity
     */
    public Payroll getPayrollById(Long id) {
        return payrollRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Payroll not found with id: " + id));
    }

    /**
     * Get all payroll records for an employee
     * 
     * @param employeeId Employee ID
     * @return List of payroll records
     */
    public List<Payroll> getPayrollsByEmployeeId(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Employee not found: " + employeeId));
        return payrollRepository.findByEmployee(employee);
    }

    /**
     * Get payroll for specific month and year
     * 
     * @param month Month name
     * @param year  Year
     * @return List of payroll records
     */
    public List<Payroll> getPayrollsByMonthAndYear(String month, Integer year) {
        return payrollRepository.findByMonthAndYear(month, year);
    }

    /**
     * Get payroll for employee in specific month/year
     * 
     * @param employeeId Employee ID
     * @param month      Month name
     * @param year       Year
     * @return Payroll record
     */
    public Payroll getPayrollByEmployeeMonthYear(Long employeeId, String month, Integer year) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Employee not found: " + employeeId));

        return payrollRepository.findByEmployeeAndMonthAndYear(employee, month, year)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Payroll not found for employee in " + month + " " + year));
    }

    /**
     * Create a payroll record
     * 
     * @param payroll Payroll entity
     * @return Created payroll
     */
    public Payroll createPayroll(Payroll payroll) {
        // Validate employee exists
        if (payroll.getEmployee() == null || payroll.getEmployee().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Employee is required");
        }

        Employee employee = employeeRepository.findById(payroll.getEmployee().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Employee not found"));

        // Check if payroll already exists for this month/year
        if (payrollRepository.existsByEmployeeAndMonthAndYear(
                employee, payroll.getMonth(), payroll.getYear())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Payroll already exists for " + payroll.getMonth() + " " + payroll.getYear());
        }

        payroll.setEmployee(employee);

        // Calculate net salary
        BigDecimal netSalary = calculateNetSalary(
                payroll.getBaseSalary(),
                payroll.getOvertimePay(),
                payroll.getDeductions());
        payroll.setNetSalary(netSalary);

        return payrollRepository.save(payroll);
    }

    // Generate payroll for employee for a specific month
    public Payroll generatePayroll(Long employeeId, String month, Integer year) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Employee not found: " + employeeId));

        // Check if payroll already exists
        if (payrollRepository.existsByEmployeeAndMonthAndYear(employee, month, year)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Payroll already exists for " + month + " " + year);
        }

        int monthNumber = getMonthNumber(month);
        LocalDate monthStart = LocalDate.of(year, monthNumber, 1);
        int daysInMonth = monthStart.lengthOfMonth();

        // Get employee's base salary
        BigDecimal fullMonthSalary = employee.getSalary() != null ? employee.getSalary() : BigDecimal.ZERO;
        BigDecimal baseSalary = fullMonthSalary;

        // Pro-rate salary if employee joined during this month
        LocalDate joiningDate = employee.getJoiningDate();
        if (joiningDate != null && joiningDate.isAfter(monthStart.minusDays(1))
                && joiningDate.isBefore(monthStart.plusDays(daysInMonth))) {

            long activeDays = java.time.temporal.ChronoUnit.DAYS.between(joiningDate, monthStart.plusDays(daysInMonth))
                    + 1;
            if (activeDays < daysInMonth) {
                baseSalary = fullMonthSalary.multiply(BigDecimal.valueOf(activeDays))
                        .divide(BigDecimal.valueOf(daysInMonth), 2, RoundingMode.HALF_UP);
            }
        }

        // Calculate overtime pay based on attendance
        BigDecimal overtimePay = calculateOvertimePay(employeeId, month, year);

        // Calculate deductions for UNPAID Leaves
        BigDecimal unpaidLeaveDeduction = calculateDeductions(employee, month, year);

        // Calculate standard deductions: Tax (10%) and PF (5%)
        BigDecimal taxDeduction = baseSalary.multiply(BigDecimal.valueOf(0.10)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal pfDeduction = baseSalary.multiply(BigDecimal.valueOf(0.05)).setScale(2, RoundingMode.HALF_UP);

        // Total Deductions = Unpaid Leaves + Tax + PF
        BigDecimal totalDeductions = unpaidLeaveDeduction.add(taxDeduction).add(pfDeduction);

        // Calculate net salary
        BigDecimal netSalary = calculateNetSalary(baseSalary, overtimePay, totalDeductions);

        Payroll payroll = new Payroll();
        payroll.setEmployee(employee);
        payroll.setMonth(month);
        payroll.setYear(year);
        payroll.setBaseSalary(baseSalary);
        payroll.setOvertimePay(overtimePay);
        payroll.setDeductions(totalDeductions);
        payroll.setNetSalary(netSalary);

        return payrollRepository.save(payroll);
    }

    /**
     * Update a payroll record
     * 
     * @param id             Payroll ID
     * @param payrollDetails Updated payroll details
     * @return Updated payroll
     */
    public Payroll updatePayroll(Long id, Payroll payrollDetails) {
        Payroll payroll = getPayrollById(id);

        if (payrollDetails.getBaseSalary() != null) {
            payroll.setBaseSalary(payrollDetails.getBaseSalary());
        }

        if (payrollDetails.getOvertimePay() != null) {
            payroll.setOvertimePay(payrollDetails.getOvertimePay());
        }

        if (payrollDetails.getDeductions() != null) {
            payroll.setDeductions(payrollDetails.getDeductions());
        }

        // Recalculate net salary
        BigDecimal netSalary = calculateNetSalary(
                payroll.getBaseSalary(),
                payroll.getOvertimePay(),
                payroll.getDeductions());
        payroll.setNetSalary(netSalary);

        return payrollRepository.save(payroll);
    }

    /**
     * Delete a payroll record
     * 
     * @param id Payroll ID
     */
    public void deletePayroll(Long id) {
        Payroll payroll = getPayrollById(id);
        payrollRepository.delete(payroll);
    }

    /**
     * Get total payroll for a month
     * 
     * @param month Month name
     * @param year  Year
     * @return Total payroll amount
     */
    public BigDecimal getTotalPayrollForMonth(String month, Integer year) {
        return payrollRepository.calculateTotalPayrollForMonth(month, year);
    }

    /**
     * Calculate net salary
     * 
     * @param baseSalary  Base salary
     * @param overtimePay Overtime pay
     * @param deductions  Deductions
     * @return Net salary
     */
    private BigDecimal calculateNetSalary(BigDecimal baseSalary,
            BigDecimal overtimePay,
            BigDecimal deductions) {
        BigDecimal base = baseSalary != null ? baseSalary : BigDecimal.ZERO;
        BigDecimal overtime = overtimePay != null ? overtimePay : BigDecimal.ZERO;
        BigDecimal deduct = deductions != null ? deductions : BigDecimal.ZERO;

        return base.add(overtime).subtract(deduct);
    }

    /**
     * Calculate overtime pay for employee in a month
     * 
     * @param employeeId Employee ID
     * @param month      Month name
     * @param year       Year
     * @return Overtime pay amount
     */
    private BigDecimal calculateOvertimePay(Long employeeId, String month, Integer year) {
        // Get month number from month name
        int monthNumber = getMonthNumber(month);

        // Get start and end dates for the month
        LocalDate startDate = LocalDate.of(year, monthNumber, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // Get all attendance records for the month
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee == null) {
            return BigDecimal.ZERO;
        }

        List<Attendance> attendanceRecords = attendanceRepository
                .findByEmployeeAndAttendanceDateBetween(employee, startDate, endDate);

        // Sum up overtime hours
        BigDecimal totalOvertimeHours = attendanceRecords.stream()
                .map(Attendance::getOvertimeHours)
                .filter(hours -> hours != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate overtime pay (assuming hourly rate = monthly salary / 160 hours *
        // 1.5)
        BigDecimal baseSalary = employee.getSalary() != null ? employee.getSalary() : BigDecimal.ZERO;
        BigDecimal hourlyRate = baseSalary.divide(
                BigDecimal.valueOf(160), 2, java.math.RoundingMode.HALF_UP);
        BigDecimal overtimeRate = hourlyRate.multiply(BigDecimal.valueOf(1.5));

        return totalOvertimeHours.multiply(overtimeRate);
    }

    /**
     * Calculate deductions for UNPAID leaves
     */
    private BigDecimal calculateDeductions(Employee employee, String month, Integer year) {
        int monthNumber = getMonthNumber(month);
        LocalDate startDate = LocalDate.of(year, monthNumber, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // Get approved UNPAID leaves for the month
        List<Leave> unpaidLeaves = leaveRepository.findApprovedLeavesForEmployeeInDateRange(employee, startDate,
                endDate);

        long unpaidDays = unpaidLeaves.stream()
                .filter(l -> "UNPAID".equalsIgnoreCase(l.getLeaveType()))
                .mapToLong(l -> {
                    LocalDate start = l.getStartDate().isBefore(startDate) ? startDate : l.getStartDate();
                    LocalDate end = l.getEndDate().isAfter(endDate) ? endDate : l.getEndDate();
                    return java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
                })
                .sum();

        if (unpaidDays == 0)
            return BigDecimal.ZERO;

        // Deduction = (Salary / 30) * unpaidDays
        BigDecimal dailyRate = (employee.getSalary() != null ? employee.getSalary() : BigDecimal.ZERO)
                .divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP);

        return dailyRate.multiply(BigDecimal.valueOf(unpaidDays));
    }

    /**
     * Get month number from month name
     * 
     * @param month Month name
     * @return Month number (1-12)
     */
    private int getMonthNumber(String month) {
        return switch (month.toUpperCase()) {
            case "JANUARY" -> 1;
            case "FEBRUARY" -> 2;
            case "MARCH" -> 3;
            case "APRIL" -> 4;
            case "MAY" -> 5;
            case "JUNE" -> 6;
            case "JULY" -> 7;
            case "AUGUST" -> 8;
            case "SEPTEMBER" -> 9;
            case "OCTOBER" -> 10;
            case "NOVEMBER" -> 11;
            case "DECEMBER" -> 12;
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid month name: " + month);
        };
    }
}
