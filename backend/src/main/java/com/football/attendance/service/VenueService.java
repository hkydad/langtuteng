package com.football.attendance.service;

import com.football.attendance.entity.Venue;
import com.football.attendance.mapper.VenueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VenueService {

    @Autowired
    private VenueMapper venueMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Venue> list() {
        return jdbcTemplate.query(
            "SELECT * FROM venue ORDER BY id",
            (rs, rowNum) -> {
                Venue v = new Venue();
                v.setId(rs.getLong("id"));
                v.setName(rs.getString("name"));
                v.setAddress(rs.getString("address"));
                return v;
            });
    }

    public Venue save(Venue venue) {
        if (venue.getId() == null) {
            Number maxIdNum = jdbcTemplate.queryForObject("SELECT MAX(id) FROM venue", Number.class);
            venue.setId(maxIdNum == null ? 1 : maxIdNum.longValue() + 1);
            jdbcTemplate.update("INSERT INTO venue (id, name, address, created_at) VALUES (?, ?, ?, ?)",
                venue.getId(), venue.getName(), venue.getAddress(), LocalDateTime.now());
        } else {
            jdbcTemplate.update("UPDATE venue SET name = ?, address = ? WHERE id = ?",
                venue.getName(), venue.getAddress(), venue.getId());
        }
        return venue;
    }

    public int delete(Long id) {
        return jdbcTemplate.update("DELETE FROM venue WHERE id = ?", id);
    }
}
