package com.football.attendance.controller;

import com.football.attendance.entity.Attendance;
import com.football.attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendances")
@CrossOrigin
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/match/{matchId}")
    public List<Map<String, Object>> getByMatchId(@PathVariable Long matchId) {
        return attendanceService.getByMatchId(matchId);
    }

    @PostMapping
    public Attendance save(@RequestBody Attendance attendance) {
        return attendanceService.save(attendance);
    }

    @PostMapping("/batch")
    public void batchSave(@RequestBody Map<String, Object> params) {
        Long matchId = Long.valueOf(params.get("matchId").toString());
        @SuppressWarnings("unchecked")
        List<Long> playerIds = (List<Long>) params.get("playerIds");
        attendanceService.batchSave(matchId, playerIds);
    }

    @GetMapping("/summary")
    public List<Map<String, Object>> getSummary(@RequestParam(required = false) Integer season) {
        return attendanceService.getSummary(season);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        attendanceService.deleteById(id);
    }
}
