package com.attendance.controller;

import com.attendance.entity.Payroll;
import com.attendance.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payroll")
@CrossOrigin(origins = "*")
public class PayrollController {

    @Autowired
    private PayrollService payrollService;

    /**
     * Get all payroll records
     * GET /api/payroll
     */
    @GetMapping
    public ResponseEntity<List<Payroll>> getAllPayrolls() {
        List<Payroll> payrolls = payrollService.getAllPayrolls();
        return ResponseEntity.ok(payrolls);
    }

    /**
     * Get payroll by ID
     * GET /api/payroll/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Payroll> getPayrollById(@PathVariable Long id) {
        Payroll payroll = payrollService.getPayrollById(id);
        return ResponseEntity.ok(payroll);
    }

    /**
     * Get payrolls for an employee
     * GET /api/payroll/employee/{employeeId}
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Payroll>> getPayrollsByEmployeeId(@PathVariable Long employeeId) {
        List<Payroll> payrolls = payrollService.getPayrollsByEmployeeId(employeeId);
        return ResponseEntity.ok(payrolls);
    }

    /**
     * Get payrolls for a specific month and year
     * GET /api/payroll/month/{month}/year/{year}
     */
    @GetMapping("/month/{month}/year/{year}")
    public ResponseEntity<List<Payroll>> getPayrollsByMonthAndYear(
            @PathVariable String month,
            @PathVariable Integer year) {
        List<Payroll> payrolls = payrollService.getPayrollsByMonthAndYear(month, year);
        return ResponseEntity.ok(payrolls);
    }

    /**
     * Get payroll for employee in specific month/year
     * GET /api/payroll/employee/{employeeId}/month/{month}/year/{year}
     */
    @GetMapping("/employee/{employeeId}/month/{month}/year/{year}")
    public ResponseEntity<Payroll> getPayrollByEmployeeMonthYear(
            @PathVariable Long employeeId,
            @PathVariable String month,
            @PathVariable Integer year) {
        Payroll payroll = payrollService.getPayrollByEmployeeMonthYear(employeeId, month, year);
        return ResponseEntity.ok(payroll);
    }

    /**
     * Create a payroll record
     * POST /api/payroll
     */
    @PostMapping
    public ResponseEntity<Payroll> createPayroll(@RequestBody Payroll payroll) {
        Payroll createdPayroll = payrollService.createPayroll(payroll);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayroll);
    }

    /**
     * Generate payroll for employee
     * POST /api/payroll/generate
     */
    @PostMapping("/generate")
    public ResponseEntity<Payroll> generatePayroll(
            @RequestParam Long employeeId,
            @RequestParam String month,
            @RequestParam Integer year) {
        Payroll payroll = payrollService.generatePayroll(employeeId, month, year);
        return ResponseEntity.status(HttpStatus.CREATED).body(payroll);
    }

    /**
     * Update a payroll record
     * PUT /api/payroll/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Payroll> updatePayroll(
            @PathVariable Long id,
            @RequestBody Payroll payrollDetails) {
        Payroll updatedPayroll = payrollService.updatePayroll(id, payrollDetails);
        return ResponseEntity.ok(updatedPayroll);
    }

    /**
     * Delete a payroll record
     * DELETE /api/payroll/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayroll(@PathVariable Long id) {
        payrollService.deletePayroll(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get total payroll for a month
     * GET /api/payroll/total/month/{month}/year/{year}
     */
    @GetMapping("/total/month/{month}/year/{year}")
    public ResponseEntity<Map<String, Object>> getTotalPayrollForMonth(
            @PathVariable String month,
            @PathVariable Integer year) {
        BigDecimal total = payrollService.getTotalPayrollForMonth(month, year);
        Map<String, Object> response = new HashMap<>();
        response.put("month", month);
        response.put("year", year);
        response.put("totalPayroll", total);
        response.put("currency", "USD");
        return ResponseEntity.ok(response);
    }
}
