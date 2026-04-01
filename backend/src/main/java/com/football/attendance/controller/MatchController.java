package com.football.attendance.controller;

import com.football.attendance.entity.Match;
import com.football.attendance.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@CrossOrigin
public class MatchController {

    @Autowired
    private MatchService matchService;

    @GetMapping
    public List<Match> list() {
        return matchService.list();
    }

    @GetMapping("/{id}")
    public Match getById(@PathVariable Long id) {
        return matchService.getById(id);
    }

    @PostMapping
    public Match save(@RequestBody Match match) {
        matchService.save(match);
        return match;
    }

    @PutMapping("/{id}")
    public Match update(@PathVariable Long id, @RequestBody Match match) {
        match.setId(id);
        matchService.update(match);
        return match;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        matchService.delete(id);
    }
}
