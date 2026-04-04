package com.football.attendance.controller;

import com.football.attendance.entity.Season;
import com.football.attendance.service.SeasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seasons")
@CrossOrigin
public class SeasonController {

    @Autowired
    private SeasonService seasonService;

    @GetMapping
    public List<Season> list() {
        return seasonService.list();
    }

    @PostMapping
    public Season save(@RequestBody Season season) {
        return seasonService.save(season);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        seasonService.delete(id);
    }
}
