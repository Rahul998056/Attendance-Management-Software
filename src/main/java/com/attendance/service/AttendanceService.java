package com.attendance.service;

import com.attendance.entity.Attendance;
import com.attendance.entity.Employee;
import com.attendance.repository.AttendanceRepository;
import com.attendance.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    // Add a new employee
    public Employee addEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    // Get all employees
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // Punch In
    public Attendance punchIn(Long employeeId) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found: " + employeeId);
        }

        // Check if already punched in for today
        List<Attendance> todayRecords = attendanceRepository.findByEmployee(employeeOpt.get());
        LocalDate today = LocalDate.now();
        Optional<Attendance> existingRecord = todayRecords.stream()
                .filter(a -> a.getAttendanceDate().equals(today))
                .findFirst();

        if (existingRecord.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already punched in for today");
        }

        Attendance attendance = new Attendance();
        attendance.setEmployee(employeeOpt.get());
        attendance.setAttendanceDate(today);
        attendance.setPunchIn(LocalDateTime.now());
        attendance.setStatus("PRESENT");

        return attendanceRepository.save(attendance);
    }

    // Punch Out
    public Attendance punchOut(Long employeeId) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found: " + employeeId);
        }

        List<Attendance> todayRecords = attendanceRepository.findByEmployee(employeeOpt.get());
        LocalDate today = LocalDate.now();
        Attendance record = todayRecords.stream()
                .filter(a -> a.getAttendanceDate().equals(today))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No punch-in record found for today"));

        if (record.getPunchOut() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already punched out for today");
        }

        record.setPunchOut(LocalDateTime.now());

        // Calculate total hours and overtime (assuming 8 hours shift)
        if (record.getPunchIn() != null && record.getPunchOut() != null) {
            long minutesWorked = Duration.between(record.getPunchIn(), record.getPunchOut()).toMinutes();
            BigDecimal hoursWorked = BigDecimal.valueOf(minutesWorked).divide(BigDecimal.valueOf(60), 2,
                    RoundingMode.HALF_UP);
            record.setTotalHours(hoursWorked);

            if (hoursWorked.compareTo(BigDecimal.valueOf(8)) > 0) {
                record.setOvertimeHours(hoursWorked.subtract(BigDecimal.valueOf(8)));
            } else {
                record.setOvertimeHours(BigDecimal.ZERO);
            }
        }

        return attendanceRepository.save(record);
    }

    // Get attendance by date (Report)
    public List<Attendance> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByAttendanceDate(date);
    }

    // Get specific employee attendance records
    public List<Attendance> getEmployeeAttendance(Long employeeId) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        return employeeOpt.map(attendanceRepository::findByEmployee).orElse(Collections.emptyList());
    }

    // Get all attendance records
    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }
}
