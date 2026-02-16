package com.attendance.controller;

import com.attendance.entity.Attendance;
import com.attendance.entity.Employee;
import com.attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private com.attendance.service.JwtUtil jwtUtil;
    // Add a new employee
    @PostMapping("/employees")
    public Employee addEmployee(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                @RequestBody Employee employee) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        return attendanceService.addEmployee(adminId, employee);
    }

    // Get all employees
    @GetMapping("/employees")
    public List<Employee> getAllEmployees(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        return attendanceService.getAllEmployees(adminId);
    }

    @GetMapping("/employees/{id}")
    public Employee getEmployeeById(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                    @PathVariable Long id) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        return attendanceService.getEmployeeById(adminId, id);
    }

    @PutMapping("/employees/{id}")
    public Employee updateEmployee(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                   @PathVariable Long id, @RequestBody Employee employee) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        return attendanceService.updateEmployee(adminId, id, employee);
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Void> deleteEmployee(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                               @PathVariable Long id) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        attendanceService.deleteEmployee(adminId, id);
        return ResponseEntity.noContent().build();
    }

    // Punch In
    @PostMapping("/attendance/punch-in")
    public Attendance punchIn(@RequestHeader(value = "Authorization", required = false) String authHeader,
                              @RequestParam Long employeeId) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        return attendanceService.punchIn(adminId, employeeId);
    }

    // Punch Out
    @PostMapping("/attendance/punch-out")
    public Attendance punchOut(@RequestHeader(value = "Authorization", required = false) String authHeader,
                               @RequestParam Long employeeId) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        return attendanceService.punchOut(adminId, employeeId);
    }

    // Get attendance for a specific date (for the Dashboard list)
    @GetMapping("/attendance/date/{date}")
    public List<Attendance> getAttendanceByDate(@RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        return attendanceService.getAttendanceByDate(adminId, date);
    }

    // Get attendance specifically for an employee
    @GetMapping("/attendance/employee/{employeeId}")
    public List<Attendance> getEmployeeAttendance(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                 @PathVariable Long employeeId) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        return attendanceService.getEmployeeAttendance(adminId, employeeId);
    }

    // Get today's attendance for a specific employee
    @GetMapping("/attendance/employee/{employeeId}/date/{date}")
    public Attendance getTodayAttendance(@RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long employeeId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        return attendanceService.getTodayAttendance(adminId, employeeId, date)
                .orElse(null); // Return null or 404 depending on preference, frontend handles null
    }

    // Get all attendance records (for initial view or unfiltered)
    @GetMapping("/attendance")
    public List<Attendance> getAllAttendance(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        return attendanceService.getAllAttendance(adminId);
    }

    // Delete attendance record
    @DeleteMapping("/attendance/{id}")
    public void deleteAttendance(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                 @PathVariable Long id) {
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.isTokenValid(token)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }
        Long adminId = jwtUtil.extractAdminId(token);
        attendanceService.deleteAttendance(adminId, id);
    }
}
