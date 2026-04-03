package com.football.attendance.controller;

import com.football.attendance.entity.Team;
import com.football.attendance.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@CrossOrigin
public class TeamController {

    @Autowired
    private TeamService teamService;

    @GetMapping
    public List<Team> list() {
        return teamService.list();
    }

    @PostMapping
    public Team save(@RequestBody Team team) {
        return teamService.save(team);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        teamService.delete(id);
    }
}
