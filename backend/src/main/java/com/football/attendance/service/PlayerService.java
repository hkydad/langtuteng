package com.football.attendance.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.football.attendance.entity.Player;
import com.football.attendance.mapper.PlayerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlayerService {

    @Autowired
    private PlayerMapper playerMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Player> list() {
        return playerMapper.selectList(new QueryWrapper<>());
    }

    public Player getById(Long id) {
        return playerMapper.selectById(id);
    }

    public Player save(Player player) {
        if (player.getId() == null) {
            Number maxIdNum = jdbcTemplate.queryForObject("SELECT MAX(id) FROM player", Number.class);
            player.setId(maxIdNum == null ? 1 : maxIdNum.longValue() + 1);
            jdbcTemplate.update("INSERT INTO player (id, name, phone, member_level, team_name, created_at) VALUES (?, ?, ?, ?, ?, ?)",
                player.getId(), player.getName(), player.getPhone(), player.getMemberLevel(), player.getTeamName(), LocalDateTime.now());
        } else {
            update(player);
        }
        return player;
    }

    public int update(Player player) {
        return jdbcTemplate.update("UPDATE player SET name = ?, phone = ?, member_level = ?, team_name = ? WHERE id = ?",
            player.getName(), player.getPhone(), player.getMemberLevel(), player.getTeamName(), player.getId());
    }

    public int delete(Long id) {
        return playerMapper.deleteById(id);
    }
}
