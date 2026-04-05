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

    public List<Player> listBySeason(Integer season) {
        if (season == null) {
            return list();
        }
        return jdbcTemplate.query(
            "SELECT * FROM player WHERE season = ? ORDER BY id",
            (rs, rowNum) -> {
                Player p = new Player();
                p.setId(rs.getLong("id"));
                p.setName(rs.getString("name"));
                p.setPhone(rs.getString("phone"));
                p.setMemberLevel(rs.getObject("member_level") != null ? rs.getInt("member_level") : null);
                p.setTeamName(rs.getString("team_name"));
                p.setSeason(rs.getObject("season") != null ? rs.getInt("season") : null);
                return p;
            }, season);
    }

    public Player getById(Long id) {
        return playerMapper.selectById(id);
    }

    public Player save(Player player) {
        if (player.getId() == null) {
            Number maxIdNum = jdbcTemplate.queryForObject("SELECT MAX(id) FROM player", Number.class);
            player.setId(maxIdNum == null ? 1 : maxIdNum.longValue() + 1);
        }
        if (player.getSeason() == null) {
            return player;
        }
        jdbcTemplate.update("INSERT INTO player (id, name, phone, member_level, team_name, season, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
            player.getId(), player.getName(), player.getPhone(), player.getMemberLevel(), player.getTeamName(), player.getSeason(), LocalDateTime.now());
        return player;
    }

    public int update(Player player) {
        return jdbcTemplate.update("UPDATE player SET name = ?, phone = ?, member_level = ?, team_name = ?, season = ? WHERE id = ?",
            player.getName(), player.getPhone(), player.getMemberLevel(), player.getTeamName(), player.getSeason(), player.getId());
    }

    public int delete(Long id) {
        return playerMapper.deleteById(id);
    }

    public int batchUpdateMemberLevel(Integer fromLevel, Integer toLevel) {
        return jdbcTemplate.update("UPDATE player SET member_level = ? WHERE member_level = ?", toLevel, fromLevel);
    }
}
