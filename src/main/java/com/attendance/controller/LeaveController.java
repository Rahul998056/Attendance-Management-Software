package com.attendance.controller;

import com.attendance.entity.Leave;
import com.attendance.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leaves")
@CrossOrigin(origins = "*")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private com.attendance.service.JwtUtil jwtUtil;

    /**
     * Get all leave requests
     * GET /api/leaves
     */
    @GetMapping
    public ResponseEntity<List<Leave>> getAllLeaves(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        List<Leave> leaves = leaveService.getAllLeaves(adminId);
        return ResponseEntity.ok(leaves);
    }

    /**
     * Get leave by ID
     * GET /api/leaves/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Leave> getLeaveById(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                              @PathVariable Long id) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        Leave leave = leaveService.getLeaveById(adminId, id);
        return ResponseEntity.ok(leave);
    }

    /**
     * Get leaves for an employee
     * GET /api/leaves/employee/{employeeId}
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Leave>> getLeavesByEmployeeId(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                             @PathVariable Long employeeId) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        List<Leave> leaves = leaveService.getLeavesByEmployeeId(adminId, employeeId);
        return ResponseEntity.ok(leaves);
    }

    /**
     * Get leaves by status
     * GET /api/leaves/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Leave>> getLeavesByStatus(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                         @PathVariable String status) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        List<Leave> leaves = leaveService.getLeavesByStatus(adminId, status);
        return ResponseEntity.ok(leaves);
    }

    /**
     * Get pending leave requests
     * GET /api/leaves/pending
     */
    @GetMapping("/pending")
    public ResponseEntity<List<Leave>> getPendingLeaves(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        List<Leave> leaves = leaveService.getPendingLeaves(adminId);
        return ResponseEntity.ok(leaves);
    }

    /**
     * Get leaves by employee and status
     * GET /api/leaves/employee/{employeeId}/status/{status}
     */
    @GetMapping("/employee/{employeeId}/status/{status}")
    public ResponseEntity<List<Leave>> getLeavesByEmployeeAndStatus(@RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long employeeId,
            @PathVariable String status) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        List<Leave> leaves = leaveService.getLeavesByEmployeeAndStatus(adminId, employeeId, status);
        return ResponseEntity.ok(leaves);
    }

    /**
     * Get leaves by type
     * GET /api/leaves/type/{leaveType}
     */
    @GetMapping("/type/{leaveType}")
    public ResponseEntity<List<Leave>> getLeavesByType(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                       @PathVariable String leaveType) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        List<Leave> leaves = leaveService.getLeavesByType(adminId, leaveType);
        return ResponseEntity.ok(leaves);
    }

    /**
     * Get leaves in date range
     * GET /api/leaves/date-range?startDate={start}&endDate={end}
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<Leave>> getLeavesInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Leave> leaves = leaveService.getLeavesInDateRange(startDate, endDate);
        return ResponseEntity.ok(leaves);
    }

    /**
     * Create a leave request
     * POST /api/leaves
     */
    @PostMapping
    public ResponseEntity<Leave> createLeave(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                             @RequestBody Leave leave) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        Leave createdLeave = leaveService.createLeave(adminId, leave);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLeave);
    }

    /**
     * Update a leave request
     * PUT /api/leaves/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Leave> updateLeave(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                             @PathVariable Long id, @RequestBody Leave leaveDetails) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        Leave updatedLeave = leaveService.updateLeave(adminId, id, leaveDetails);
        return ResponseEntity.ok(updatedLeave);
    }

    /**
     * Approve a leave request
     * PATCH /api/leaves/{id}/approve
     */
    @PatchMapping("/{id}/approve")
    public ResponseEntity<Leave> approveLeave(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                              @PathVariable Long id) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        Leave approvedLeave = leaveService.approveLeave(adminId, id);
        return ResponseEntity.ok(approvedLeave);
    }

    /**
     * Reject a leave request
     * PATCH /api/leaves/{id}/reject
     */
    @PatchMapping("/{id}/reject")
    public ResponseEntity<Leave> rejectLeave(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                             @PathVariable Long id) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        Leave rejectedLeave = leaveService.rejectLeave(adminId, id);
        return ResponseEntity.ok(rejectedLeave);
    }

    /**
     * Delete a leave request
     * DELETE /api/leaves/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeave(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                            @PathVariable Long id) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        leaveService.deleteLeave(adminId, id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Calculate leave days
     * GET /api/leaves/calculate-days?startDate={start}&endDate={end}
     */
    @GetMapping("/calculate-days")
    public ResponseEntity<Map<String, Object>> calculateLeaveDays(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        long days = leaveService.calculateLeaveDays(startDate, endDate);
        Map<String, Object> response = new HashMap<>();
        response.put("startDate", startDate);
        response.put("endDate", endDate);
        response.put("totalDays", days);
        return ResponseEntity.ok(response);
    }

    /**
     * Get total leave days for employee in a year
     * GET /api/leaves/employee/{employeeId}/year/{year}/total-days
     */
    @GetMapping("/employee/{employeeId}/year/{year}/total-days")
    public ResponseEntity<Map<String, Object>> getTotalLeaveDaysForYear(
            @PathVariable Long employeeId,
            @PathVariable int year) {
        long totalDays = leaveService.getTotalLeaveDaysForYear(employeeId, year);
        Map<String, Object> response = new HashMap<>();
        response.put("employeeId", employeeId);
        response.put("year", year);
        response.put("totalLeaveDays", totalDays);
        return ResponseEntity.ok(response);
    }

    /**
     * Get remaining leave balance
     * GET /api/leaves/employee/{employeeId}/year/{year}/balance?quota={quota}
     */
    @GetMapping("/employee/{employeeId}/year/{year}/balance")
    public ResponseEntity<Map<String, Object>> getRemainingLeaveBalance(
            @PathVariable Long employeeId,
            @PathVariable int year,
            @RequestParam(defaultValue = "20") long quota) {
        long usedDays = leaveService.getTotalLeaveDaysForYear(employeeId, year);
        long remaining = leaveService.getRemainingLeaveBalance(employeeId, year, quota);

        Map<String, Object> response = new HashMap<>();
        response.put("employeeId", employeeId);
        response.put("year", year);
        response.put("annualQuota", quota);
        response.put("usedDays", usedDays);
        response.put("remainingDays", remaining);
        return ResponseEntity.ok(response);
    }
}
