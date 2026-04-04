package com.football.attendance.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.football.attendance.entity.Match;
import com.football.attendance.mapper.MatchMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MatchService {

    @Autowired
    private MatchMapper matchMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Match> list() {
        QueryWrapper<Match> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("match_date");
        return matchMapper.selectList(wrapper);
    }

    public List<Match> listBySeason(Integer season) {
        if (season == null) {
            return list();
        }
        return jdbcTemplate.query(
            "SELECT * FROM football_match WHERE season = ? ORDER BY match_date DESC",
            (rs, rowNum) -> {
                Match m = new Match();
                m.setId(rs.getLong("id"));
                m.setMatchDate(LocalDate.parse(rs.getString("match_date")));
                m.setLocation(rs.getString("location"));
                m.setSeason(rs.getObject("season") != null ? rs.getInt("season") : null);
                return m;
            }, season);
    }

    public Match getById(Long id) {
        return matchMapper.selectById(id);
    }

    public Match save(Match match) {
        if (match.getId() == null) {
            Number maxIdNum = jdbcTemplate.queryForObject("SELECT MAX(id) FROM football_match", Number.class);
            match.setId(maxIdNum == null ? 1 : maxIdNum.longValue() + 1);
        }
        if (match.getSeason() == null) {
            match.setSeason(match.getMatchDate().getYear());
        }
        jdbcTemplate.update("INSERT INTO football_match (id, match_date, location, season, created_at) VALUES (?, ?, ?, ?, ?)",
            match.getId(), match.getMatchDate().toString(), match.getLocation(), match.getSeason(), LocalDateTime.now());
        return match;
    }

    public int update(Match match) {
        if (match.getSeason() == null) {
            match.setSeason(match.getMatchDate().getYear());
        }
        return jdbcTemplate.update("UPDATE football_match SET match_date = ?, location = ?, season = ? WHERE id = ?",
            match.getMatchDate().toString(), match.getLocation(), match.getSeason(), match.getId());
    }

    public int delete(Long id) {
        return matchMapper.deleteById(id);
    }
}
