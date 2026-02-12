package com.attendance.controller;

import com.attendance.entity.Attendance;
import com.attendance.entity.Employee;
import com.attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    // Add a new employee
    @PostMapping("/employees")
    public Employee addEmployee(@RequestBody Employee employee) {
        return attendanceService.addEmployee(employee);
    }

    // Get all employees
    @GetMapping("/employees")
    public List<Employee> getAllEmployees() {
        return attendanceService.getAllEmployees();
    }

    // Punch In
    @PostMapping("/attendance/punch-in")
    public Attendance punchIn(@RequestParam Long employeeId) {
        return attendanceService.punchIn(employeeId);
    }

    // Punch Out
    @PostMapping("/attendance/punch-out")
    public Attendance punchOut(@RequestParam Long employeeId) {
        return attendanceService.punchOut(employeeId);
    }

    // Get attendance for a specific date (for the Dashboard list)
    @GetMapping("/attendance/date/{date}")
    public List<Attendance> getAttendanceByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return attendanceService.getAttendanceByDate(date);
    }

    // Get attendance specifically for an employee
    @GetMapping("/attendance/employee/{employeeId}")
    public List<Attendance> getEmployeeAttendance(@PathVariable Long employeeId) {
        return attendanceService.getEmployeeAttendance(employeeId);
    }

    // Get all attendance records (for initial view or unfiltered)
    @GetMapping("/attendance")
    public List<Attendance> getAllAttendance() {
        return attendanceService.getAllAttendance();
    }
}
