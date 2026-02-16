package com.attendance.service;

import com.attendance.entity.Attendance;
import com.attendance.entity.Break;
import com.attendance.repository.AttendanceRepository;
import com.attendance.repository.BreakRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BreakService {

    @Autowired
    private BreakRepository breakRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    /**
     * Get all breaks
     * 
     * @return List of all breaks
     */
    public List<Break> getAllBreaks() {
        return breakRepository.findAll();
    }

    /**
     * Get break by ID
     * 
     * @param id Break ID
     * @return Break entity
     */
    public Break getBreakById(Long id) {
        return breakRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Break not found with id: " + id));
    }

    /**
     * Get all breaks for an attendance record
     * 
     * @param attendanceId Attendance ID
     * @return List of breaks
     */
    public List<Break> getBreaksByAttendanceId(Long attendanceId) {
        return breakRepository.findByAttendanceId(attendanceId);
    }

    /**
     * Start a break
     * 
     * @param attendanceId Attendance ID
     * @return Created break record
     */
    public Break startBreak(Long attendanceId) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Attendance record not found: " + attendanceId));

        // Check if there's already an active break
        List<Break> activeBreaks = breakRepository.findActiveBreaks();
        for (Break activeBreak : activeBreaks) {
            if (activeBreak.getAttendance().getId().equals(attendanceId)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Break already in progress");
            }
        }

        Break breakRecord = new Break();
        breakRecord.setAttendance(attendance);
        breakRecord.setBreakStart(LocalDateTime.now());

        return breakRepository.save(breakRecord);
    }

    /**
     * End a break
     * 
     * @param breakId Break ID
     * @return Updated break record
     */
    public Break endBreak(Long breakId) {
        Break breakRecord = getBreakById(breakId);

        if (breakRecord.getBreakEnd() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Break already ended");
        }

        breakRecord.setBreakEnd(LocalDateTime.now());

        // Calculate break duration
        long minutes = Duration.between(
                breakRecord.getBreakStart(),
                breakRecord.getBreakEnd()).toMinutes();

        BigDecimal duration = BigDecimal.valueOf(minutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        breakRecord.setBreakDuration(duration);

        return breakRepository.save(breakRecord);
    }

    /**
     * Create a break record manually
     * 
     * @param breakRecord Break entity
     * @return Created break
     */
    public Break createBreak(Break breakRecord) {
        // Validate attendance exists
        if (breakRecord.getAttendance() == null || breakRecord.getAttendance().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Attendance record is required");
        }

        Attendance attendance = attendanceRepository.findById(breakRecord.getAttendance().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Attendance record not found"));

        breakRecord.setAttendance(attendance);

        // Calculate duration if both start and end are provided
        if (breakRecord.getBreakStart() != null && breakRecord.getBreakEnd() != null) {
            long minutes = Duration.between(
                    breakRecord.getBreakStart(),
                    breakRecord.getBreakEnd()).toMinutes();

            BigDecimal duration = BigDecimal.valueOf(minutes)
                    .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

            breakRecord.setBreakDuration(duration);
        }

        return breakRepository.save(breakRecord);
    }

    /**
     * Update a break record
     * 
     * @param id           Break ID
     * @param breakDetails Updated break details
     * @return Updated break
     */
    public Break updateBreak(Long id, Break breakDetails) {
        Break breakRecord = getBreakById(id);

        if (breakDetails.getBreakStart() != null) {
            breakRecord.setBreakStart(breakDetails.getBreakStart());
        }

        if (breakDetails.getBreakEnd() != null) {
            breakRecord.setBreakEnd(breakDetails.getBreakEnd());
        }

        // Recalculate duration if both times are set
        if (breakRecord.getBreakStart() != null && breakRecord.getBreakEnd() != null) {
            long minutes = Duration.between(
                    breakRecord.getBreakStart(),
                    breakRecord.getBreakEnd()).toMinutes();

            BigDecimal duration = BigDecimal.valueOf(minutes)
                    .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

            breakRecord.setBreakDuration(duration);
        }

        return breakRepository.save(breakRecord);
    }

    /**
     * Delete a break
     * 
     * @param id Break ID
     */
    public void deleteBreak(Long id) {
        Break breakRecord = getBreakById(id);
        breakRepository.delete(breakRecord);
    }

    /**
     * Get total break duration for an attendance record
     * 
     * @param attendanceId Attendance ID
     * @return Total break duration in hours
     */
    public BigDecimal getTotalBreakDuration(Long attendanceId) {
        return breakRepository.calculateTotalBreakDuration(attendanceId);
    }

    /**
     * Get all active breaks
     * 
     * @return List of active breaks
     */
    public List<Break> getActiveBreaks() {
        return breakRepository.findActiveBreaks();
    }
}
