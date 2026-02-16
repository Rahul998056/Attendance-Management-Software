package com.attendance.repository;

import com.attendance.entity.AdminCredential;
import com.attendance.entity.Attendance;
import com.attendance.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByEmployee(Employee employee);

    List<Attendance> findByEmployeeAndAttendanceDate(Employee employee, LocalDate attendanceDate);

    List<Attendance> findByAttendanceDate(LocalDate attendanceDate);

    List<Attendance> findByEmployeeAndAttendanceDateBetween(Employee employee, LocalDate startDate, LocalDate endDate);
    
    // Admin-filtered queries
    List<Attendance> findByAdmin(AdminCredential admin);
    
    List<Attendance> findByAdminAndAttendanceDate(AdminCredential admin, LocalDate attendanceDate);
    
    List<Attendance> findByAdminAndEmployeeAndAttendanceDateBetween(AdminCredential admin, Employee employee, LocalDate startDate, LocalDate endDate);
    
    Optional<Attendance> findByAdminAndId(AdminCredential admin, Long id);
}
