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

    public Match getById(Long id) {
        return matchMapper.selectById(id);
    }

    public Match save(Match match) {
        if (match.getId() == null) {
            Long maxId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM football_match", Long.class);
            match.setId(maxId == null ? 1 : maxId + 1);
        }
        jdbcTemplate.update("INSERT INTO football_match (id, match_date, location, created_at) VALUES (?, ?, ?, ?)",
            match.getId(), match.getMatchDate(), match.getLocation(), LocalDateTime.now());
        return match;
    }

    public int update(Match match) {
        return matchMapper.updateById(match);
    }

    public int delete(Long id) {
        return matchMapper.deleteById(id);
    }
}
