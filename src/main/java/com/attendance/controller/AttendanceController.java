package com.attendance.controller;

import com.attendance.entity.Attendance;
import com.attendance.entity.Student;
import com.attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    // Add a new student
    @PostMapping("/students")
    public Student addStudent(@RequestBody Student student) {
        return attendanceService.addStudent(student);
    }

    // Get all students
    @GetMapping("/students")
    public List<Student> getAllStudents() {
        return attendanceService.getAllStudents();
    }

    // Mark attendance
    // Use request param or body. Since it's simple, I'll use params for now, 
    // or better, a DTO, but for interview simplicity, params are okay if clear.
    @PostMapping("/attendance/mark")
    public Attendance markAttendance(@RequestParam Long studentId, @RequestParam boolean present) {
        return attendanceService.markAttendance(studentId, present);
    }

    // Get attendance for a specific date (Report)
    @GetMapping("/attendance/date/{date}")
    public List<Attendance> getAttendanceByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return attendanceService.getAttendanceByDate(date);
    }

    // Get attendance specifically for a student
    @GetMapping("/attendance/student/{studentId}")
    public List<Attendance> getStudentAttendance(@PathVariable Long studentId) {
        return attendanceService.getStudentAttendance(studentId);
    }
}
