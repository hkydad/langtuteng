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
    public List<Attendance> getByMatchId(@PathVariable Long matchId) {
        return attendanceService.getByMatchId(matchId);
    }

    @PostMapping
    public Attendance save(@RequestBody Attendance attendance) {
        return attendanceService.save(attendance);
    }

    @GetMapping("/summary")
    public List<Map<String, Object>> getSummary() {
        return attendanceService.getSummary();
    }
}
