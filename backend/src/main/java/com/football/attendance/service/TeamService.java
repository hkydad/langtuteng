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

    public Team save(Team team) {
        if (team.getId() == null) {
            Number maxIdNum = jdbcTemplate.queryForObject("SELECT MAX(id) FROM team", Number.class);
            team.setId(maxIdNum == null ? 1 : maxIdNum.longValue() + 1);
            jdbcTemplate.update("INSERT INTO team (id, name, created_at) VALUES (?, ?, ?)",
                team.getId(), team.getName(), LocalDateTime.now());
        } else {
            jdbcTemplate.update("UPDATE team SET name = ? WHERE id = ?", team.getName(), team.getId());
        }
        return team;
    }

    public int delete(Long id) {
        return teamMapper.deleteById(id);
    }
}
