package com.football.attendance.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.football.attendance.entity.Team;
import com.football.attendance.mapper.TeamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TeamService {

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Team> list() {
        return teamMapper.selectList(new QueryWrapper<>());
    }

    public List<Team> listBySeason(Integer season) {
        if (season == null) {
            return list();
        }
        return jdbcTemplate.query(
            "SELECT * FROM team WHERE season = ? ORDER BY id",
            (rs, rowNum) -> {
                Team t = new Team();
                t.setId(rs.getLong("id"));
                t.setName(rs.getString("name"));
                t.setSeason(rs.getObject("season") != null ? rs.getInt("season") : null);
                return t;
            }, season);
    }

    public Team save(Team team) {
        if (team.getId() == null) {
            Number maxIdNum = jdbcTemplate.queryForObject("SELECT MAX(id) FROM team", Number.class);
            team.setId(maxIdNum == null ? 1 : maxIdNum.longValue() + 1);
        }
        if (team.getSeason() == null) {
            return team;
        }
        jdbcTemplate.update("INSERT INTO team (id, name, season, created_at) VALUES (?, ?, ?, ?)",
            team.getId(), team.getName(), team.getSeason(), LocalDateTime.now());
        return team;
    }

    public int update(Team team) {
        return jdbcTemplate.update("UPDATE team SET name = ?, season = ? WHERE id = ?",
            team.getName(), team.getSeason(), team.getId());
    }

    public int delete(Long id) {
        return teamMapper.deleteById(id);
    }
}
