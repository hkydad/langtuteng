package com.football.attendance.controller;

import com.football.attendance.entity.Venue;
import com.football.attendance.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
@CrossOrigin
public class VenueController {

    @Autowired
    private VenueService venueService;

    @GetMapping
    public List<Venue> list() {
        return venueService.list();
    }

    @PostMapping
    public Venue save(@RequestBody Venue venue) {
        return venueService.save(venue);
    }

    @PutMapping("/{id}")
    public Venue update(@PathVariable Long id, @RequestBody Venue venue) {
        venue.setId(id);
        venueService.save(venue);
        return venue;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        venueService.delete(id);
    }
}
