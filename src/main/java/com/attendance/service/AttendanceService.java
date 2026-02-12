package com.attendance.service;

import com.attendance.entity.Attendance;
import com.attendance.entity.Student;
import com.attendance.repository.AttendanceRepository;
import com.attendance.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    // Add a new student
    public Student addStudent(Student student) {
        return studentRepository.save(student);
    }

    // Get all students
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Mark attendance
    public Attendance markAttendance(Long studentId, boolean isPresent) {
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found: " + studentId);
        }

        Attendance attendance = new Attendance();
        attendance.setStudent(studentOpt.get());
        attendance.setDate(LocalDate.now());
        attendance.setPresent(isPresent);
        return attendanceRepository.save(attendance);
    }

    // Get attendance by date (Report)
    public List<Attendance> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date);
    }
    
    // Get specific student attendance records
    public List<Attendance> getStudentAttendance(Long studentId) {
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        return studentOpt.map(attendanceRepository::findByStudent).orElse(Collections.emptyList());
    }
}
