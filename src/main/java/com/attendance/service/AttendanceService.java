package com.attendance.service;

import com.attendance.entity.Attendance;
import com.attendance.entity.Employee;
import com.attendance.entity.EmployeeProject;
import com.attendance.entity.Leave;
import com.attendance.entity.Payroll;
import com.attendance.entity.User;
import com.attendance.repository.AttendanceRepository;
import com.attendance.repository.EmployeeProjectRepository;
import com.attendance.repository.EmployeeRepository;
import com.attendance.repository.LeaveRepository;
import com.attendance.repository.PayrollRepository;
import com.attendance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private com.attendance.repository.AdminCredentialRepository adminCredentialRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EmployeeProjectRepository employeeProjectRepository;

    @Autowired
    private PayrollRepository payrollRepository;

    @Autowired
    private LeaveRepository leaveRepository;

    // Add a new employee (scoped to admin)
    public Employee addEmployee(Long adminId, Employee employee) {
        if (employee == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee is required");
        }

        // The UI creates employees without linking to a user account. If a user is supplied,
        // validate and attach a managed User entity to avoid FK constraint failures.
        if (employee.getUser() != null && employee.getUser().getId() != null) {
            Long userId = employee.getUser().getId();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));

            if (employeeRepository.existsByUser_Id(userId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee already exists for user id: " + userId);
            }

            employee.setUser(user);
        } else {
            employee.setUser(null);
        }

        // Set admin for this employee
        com.attendance.entity.AdminCredential admin = adminCredentialRepository.findById(adminId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));
        employee.setAdmin(admin);
        return employeeRepository.save(employee);
    }

    // Get all employees (scoped to admin)
    public List<Employee> getAllEmployees(Long adminId) {
        com.attendance.entity.AdminCredential admin = adminCredentialRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));
        return employeeRepository.findByAdmin(admin);
    }

        public Employee getEmployeeById(Long adminId, Long id) {
        com.attendance.entity.AdminCredential admin = adminCredentialRepository.findById(adminId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));
        return employeeRepository.findByAdminAndId(admin, id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found: " + id));
        }

    public Employee updateEmployee(Long adminId, Long id, Employee employeeDetails) {
        Employee employee = getEmployeeById(adminId, id);

        if (employeeDetails == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee is required");
        }

        if (employeeDetails.getFirstName() != null) {
            employee.setFirstName(employeeDetails.getFirstName());
        }
        if (employeeDetails.getLastName() != null) {
            employee.setLastName(employeeDetails.getLastName());
        }
        if (employeeDetails.getPhone() != null) {
            employee.setPhone(employeeDetails.getPhone());
        }
        if (employeeDetails.getDesignation() != null) {
            employee.setDesignation(employeeDetails.getDesignation());
        }
        if (employeeDetails.getDepartment() != null) {
            employee.setDepartment(employeeDetails.getDepartment());
        }
        if (employeeDetails.getJoiningDate() != null) {
            employee.setJoiningDate(employeeDetails.getJoiningDate());
        }
        if (employeeDetails.getSalary() != null) {
            employee.setSalary(employeeDetails.getSalary());
        }
        if (employeeDetails.getAvatar() != null) {
            employee.setAvatar(employeeDetails.getAvatar());
        }

        if (employeeDetails.getUser() != null && employeeDetails.getUser().getId() != null) {
            Long userId = employeeDetails.getUser().getId();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));

            if (employeeRepository.existsByUser_Id(userId) && (employee.getUser() == null || !userId.equals(employee.getUser().getId()))) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee already exists for user id: " + userId);
            }

            employee.setUser(user);
        }

        return employeeRepository.save(employee);
    }

    @Transactional
    public void deleteEmployee(Long adminId, Long id) {
        Employee employee = getEmployeeById(adminId, id);

        // Remove attendance rows explicitly before deleting the employee.
        jdbcTemplate.update("DELETE FROM attendance WHERE employee_id = ?", id);

        List<EmployeeProject> assignments = employeeProjectRepository.findByEmployee(employee);
        if (!assignments.isEmpty()) {
            employeeProjectRepository.deleteAll(assignments);
        }

        List<Payroll> payrolls = payrollRepository.findByEmployee(employee);
        if (!payrolls.isEmpty()) {
            payrollRepository.deleteAll(payrolls);
        }

        List<Leave> leaves = leaveRepository.findByEmployee(employee);
        if (!leaves.isEmpty()) {
            leaveRepository.deleteAll(leaves);
        }

        employeeRepository.delete(employee);
    }

    // Punch In (scoped to admin)
    public Attendance punchIn(Long adminId, Long employeeId) {
        com.attendance.entity.AdminCredential admin = adminCredentialRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));

        Optional<Employee> employeeOpt = employeeRepository.findByAdminAndId(admin, employeeId);
        if (employeeOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found: " + employeeId);
        }

        // Check if already punched in for today (admin-scoped)
        LocalDate today = LocalDate.now();
        List<Attendance> todayRecords = attendanceRepository.findByAdminAndEmployeeAndAttendanceDateBetween(admin, employeeOpt.get(), today, today);
        Optional<Attendance> existingRecord = todayRecords.stream().findFirst();

        if (existingRecord.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already punched in for today");
        }

        Attendance attendance = new Attendance();
        attendance.setEmployee(employeeOpt.get());
        attendance.setAttendanceDate(today);
        attendance.setPunchIn(LocalDateTime.now());
        attendance.setStatus("PRESENT");

        attendance.setAdmin(admin);
        return attendanceRepository.save(attendance);
    }

    // Punch Out (scoped to admin)
    public Attendance punchOut(Long adminId, Long employeeId) {
        com.attendance.entity.AdminCredential admin = adminCredentialRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));

        Optional<Employee> employeeOpt = employeeRepository.findByAdminAndId(admin, employeeId);
        if (employeeOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found: " + employeeId);
        }

        List<Attendance> todayRecords = attendanceRepository.findByAdminAndEmployeeAndAttendanceDateBetween(admin, employeeOpt.get(), LocalDate.now(), LocalDate.now());
        LocalDate today = LocalDate.now();
        Attendance record = todayRecords.stream()
                .filter(a -> today.equals(a.getAttendanceDate()))
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

    // Get attendance by date (scoped to admin)
    public List<Attendance> getAttendanceByDate(Long adminId, LocalDate date) {
        com.attendance.entity.AdminCredential admin = adminCredentialRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));
        return attendanceRepository.findByAdminAndAttendanceDate(admin, date);
    }

    // Get specific employee attendance records
    public List<Attendance> getEmployeeAttendance(Long adminId, Long employeeId) {
        com.attendance.entity.AdminCredential admin = adminCredentialRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));
        Optional<Employee> employeeOpt = employeeRepository.findByAdminAndId(admin, employeeId);
        return employeeOpt.map(e -> attendanceRepository.findByAdminAndEmployeeAndAttendanceDateBetween(admin, e, LocalDate.MIN, LocalDate.MAX))
                .orElse(Collections.emptyList());
    }

    // Get today's attendance for a specific employee
    public Optional<Attendance> getTodayAttendance(Long adminId, Long employeeId, LocalDate date) {
        com.attendance.entity.AdminCredential admin = adminCredentialRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));
        Optional<Employee> employeeOpt = employeeRepository.findByAdminAndId(admin, employeeId);
        if (employeeOpt.isEmpty()) {
            return Optional.empty();
        }
        List<Attendance> records = attendanceRepository.findByAdminAndEmployeeAndAttendanceDateBetween(admin, employeeOpt.get(), date, date);
        return records.stream().findFirst();
    }

    // Get all attendance records (scoped to admin)
    public List<Attendance> getAllAttendance(Long adminId) {
        com.attendance.entity.AdminCredential admin = adminCredentialRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));
        return attendanceRepository.findByAdmin(admin);
    }

    // Delete attendance record (scoped to admin)
    public void deleteAttendance(Long adminId, Long id) {
        com.attendance.entity.AdminCredential admin = adminCredentialRepository.findById(adminId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin not found"));
        com.attendance.entity.Attendance attendance = attendanceRepository.findByAdminAndId(admin, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance record not found: " + id));
        attendanceRepository.delete(attendance);
    }
}
