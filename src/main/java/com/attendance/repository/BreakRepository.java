package com.attendance.repository;

import com.attendance.entity.Attendance;
import com.attendance.entity.Break;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BreakRepository extends JpaRepository<Break, Long> {

    /**
     * Find all breaks for a specific attendance record
     * 
     * @param attendance The attendance record
     * @return List of breaks
     */
    List<Break> findByAttendance(Attendance attendance);

    /**
     * Find all breaks for a specific attendance ID
     * 
     * @param attendanceId The attendance ID
     * @return List of breaks
     */
    List<Break> findByAttendanceId(Long attendanceId);

    /**
     * Calculate total break duration for an attendance record
     * 
     * @param attendanceId The attendance ID
     * @return Total break duration in hours
     */
    @Query("SELECT COALESCE(SUM(b.breakDuration), 0) FROM Break b WHERE b.attendance.id = :attendanceId")
    BigDecimal calculateTotalBreakDuration(@Param("attendanceId") Long attendanceId);

    /**
     * Find all active breaks (break_end is null)
     * 
     * @return List of active breaks
     */
    @Query("SELECT b FROM Break b WHERE b.breakEnd IS NULL")
    List<Break> findActiveBreaks();
}
