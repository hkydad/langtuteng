package com.football.attendance.service;

import com.football.attendance.entity.Season;
import com.football.attendance.mapper.SeasonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SeasonService {

    @Autowired
    private SeasonMapper seasonMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Season> list() {
        return jdbcTemplate.query(
            "SELECT * FROM season ORDER BY year DESC",
            (rs, rowNum) -> {
                Season s = new Season();
                s.setId(rs.getLong("id"));
                s.setYear(rs.getInt("year"));
                s.setName(rs.getString("name"));
                return s;
            });
    }

    public Season save(Season season) {
        if (season.getId() == null) {
            Number maxIdNum = jdbcTemplate.queryForObject("SELECT MAX(id) FROM season", Number.class);
            season.setId(maxIdNum == null ? 1 : maxIdNum.longValue() + 1);
            jdbcTemplate.update("INSERT INTO season (id, year, name, created_at) VALUES (?, ?, ?, ?)",
                season.getId(), season.getYear(), season.getName(), LocalDateTime.now());
        } else {
            jdbcTemplate.update("UPDATE season SET year = ?, name = ? WHERE id = ?",
                season.getYear(), season.getName(), season.getId());
        }
        return season;
    }

    public int delete(Long id) {
        return jdbcTemplate.update("DELETE FROM season WHERE id = ?", id);
    }
}
