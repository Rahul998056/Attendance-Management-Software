package com.attendance.controller;

import com.attendance.entity.Break;
import com.attendance.service.BreakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/breaks")
@CrossOrigin(origins = "*")
public class BreakController {

    @Autowired
    private BreakService breakService;

    /**
     * Get all breaks
     * GET /api/breaks
     */
    @GetMapping
    public ResponseEntity<List<Break>> getAllBreaks() {
        List<Break> breaks = breakService.getAllBreaks();
        return ResponseEntity.ok(breaks);
    }

    /**
     * Get break by ID
     * GET /api/breaks/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Break> getBreakById(@PathVariable Long id) {
        Break breakRecord = breakService.getBreakById(id);
        return ResponseEntity.ok(breakRecord);
    }

    /**
     * Get breaks by attendance ID
     * GET /api/breaks/attendance/{attendanceId}
     */
    @GetMapping("/attendance/{attendanceId}")
    public ResponseEntity<List<Break>> getBreaksByAttendanceId(@PathVariable Long attendanceId) {
        List<Break> breaks = breakService.getBreaksByAttendanceId(attendanceId);
        return ResponseEntity.ok(breaks);
    }

    /**
     * Get active breaks
     * GET /api/breaks/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<Break>> getActiveBreaks() {
        List<Break> breaks = breakService.getActiveBreaks();
        return ResponseEntity.ok(breaks);
    }

    /**
     * Start a break
     * POST /api/breaks/start/{attendanceId}
     */
    @PostMapping("/start/{attendanceId}")
    public ResponseEntity<Break> startBreak(@PathVariable Long attendanceId) {
        Break breakRecord = breakService.startBreak(attendanceId);
        return ResponseEntity.status(HttpStatus.CREATED).body(breakRecord);
    }

    /**
     * End a break
     * PUT /api/breaks/end/{breakId}
     */
    @PutMapping("/end/{breakId}")
    public ResponseEntity<Break> endBreak(@PathVariable Long breakId) {
        Break breakRecord = breakService.endBreak(breakId);
        return ResponseEntity.ok(breakRecord);
    }

    /**
     * Create a break manually
     * POST /api/breaks
     */
    @PostMapping
    public ResponseEntity<Break> createBreak(@RequestBody Break breakRecord) {
        Break createdBreak = breakService.createBreak(breakRecord);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBreak);
    }

    /**
     * Update a break
     * PUT /api/breaks/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Break> updateBreak(@PathVariable Long id, @RequestBody Break breakDetails) {
        Break updatedBreak = breakService.updateBreak(id, breakDetails);
        return ResponseEntity.ok(updatedBreak);
    }

    /**
     * Delete a break
     * DELETE /api/breaks/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBreak(@PathVariable Long id) {
        breakService.deleteBreak(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get total break duration for attendance
     * GET /api/breaks/attendance/{attendanceId}/total-duration
     */
    @GetMapping("/attendance/{attendanceId}/total-duration")
    public ResponseEntity<Map<String, Object>> getTotalBreakDuration(@PathVariable Long attendanceId) {
        BigDecimal totalDuration = breakService.getTotalBreakDuration(attendanceId);
        Map<String, Object> response = new HashMap<>();
        response.put("attendanceId", attendanceId);
        response.put("totalBreakDuration", totalDuration);
        response.put("unit", "hours");
        return ResponseEntity.ok(response);
    }
}
