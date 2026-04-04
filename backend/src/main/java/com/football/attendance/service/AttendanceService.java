package com.football.attendance.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.football.attendance.entity.Attendance;
import com.football.attendance.entity.Player;
import com.football.attendance.mapper.AttendanceMapper;
import com.football.attendance.mapper.PlayerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Autowired
    private PlayerMapper playerMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getByMatchId(Long matchId) {
        QueryWrapper<Attendance> wrapper = new QueryWrapper<>();
        wrapper.eq("match_id", matchId);
        wrapper.eq("status", "PRESENT");
        List<Attendance> attendances = attendanceMapper.selectList(wrapper);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Attendance a : attendances) {
            Player player = playerMapper.selectById(a.getPlayerId());
            if (player != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", a.getId());
                item.put("matchId", a.getMatchId());
                item.put("playerId", player.getId());
                item.put("playerName", player.getName());
                item.put("memberLevel", player.getMemberLevel());
                item.put("phone", player.getPhone());
                item.put("status", a.getStatus());
                item.put("remark", a.getRemark());
                item.put("createdAt", a.getCreatedAt());
                result.add(item);
            }
        }
        return result;
    }

    public void batchSave(Long matchId, List<?> playerIds) {
        for (Object pid : playerIds) {
            Long playerId = ((Number) pid).longValue();
            Integer existCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM attendance WHERE match_id = ? AND player_id = ?", Integer.class, matchId, playerId);
            if (existCount != null && existCount > 0) {
                continue;
            }
            Number maxIdNum = jdbcTemplate.queryForObject("SELECT MAX(id) FROM attendance", Number.class);
            Long newId = maxIdNum == null ? 1 : maxIdNum.longValue() + 1;
            jdbcTemplate.update("INSERT INTO attendance (id, match_id, player_id, status, created_at) VALUES (?, ?, ?, ?, ?)",
                newId, matchId, playerId, "PRESENT", LocalDateTime.now());
        }
    }

    public Attendance save(Attendance attendance) {
        QueryWrapper<Attendance> wrapper = new QueryWrapper<>();
        wrapper.eq("match_id", attendance.getMatchId())
               .eq("player_id", attendance.getPlayerId());
        Attendance exist = attendanceMapper.selectOne(wrapper);

        if (exist != null) {
            attendance.setId(exist.getId());
            attendanceMapper.updateById(attendance);
        } else {
            if (attendance.getId() == null) {
                Number maxIdNum = jdbcTemplate.queryForObject("SELECT MAX(id) FROM attendance", Number.class);
                attendance.setId(maxIdNum == null ? 1 : maxIdNum.longValue() + 1);
            }
            jdbcTemplate.update("INSERT INTO attendance (id, match_id, player_id, status, remark, created_at) VALUES (?, ?, ?, ?, ?, ?)",
                attendance.getId(), attendance.getMatchId(), attendance.getPlayerId(),
                attendance.getStatus(), attendance.getRemark(), LocalDateTime.now());
        }
        return attendance;
    }

    public void deleteByMatchId(Long matchId) {
        jdbcTemplate.update("DELETE FROM attendance WHERE match_id = ?", matchId);
    }

    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM attendance WHERE id = ?", id);
    }

    public List<Map<String, Object>> getSummary(Integer season) {
        // 获取当赛季总比赛场次
        int seasonMatchCount = 0;
        if (season != null) {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM football_match WHERE season = ?", Integer.class, season);
            seasonMatchCount = count != null ? count : 0;
        } else {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM football_match", Integer.class);
            seasonMatchCount = count != null ? count : 0;
        }

        // 获取所有球员
        List<Player> players;
        if (season != null) {
            players = jdbcTemplate.query(
                "SELECT * FROM player WHERE season = ?",
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
        } else {
            players = playerMapper.selectList(new QueryWrapper<>());
        }

        List<Map<String, Object>> result = new ArrayList<>();

        for (Player player : players) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("playerId", player.getId());
            stat.put("playerName", player.getName());

            // 获取该球员在当赛季的出勤次数
            int presentCount = 0;
            if (season != null) {
                Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM attendance a " +
                    "LEFT JOIN football_match m ON a.match_id = m.id " +
                    "WHERE a.player_id = ? AND m.season = ? AND a.status = 'PRESENT'",
                    Integer.class, player.getId(), season);
                presentCount = count != null ? count : 0;
            } else {
                QueryWrapper<Attendance> wrapper = new QueryWrapper<>();
                wrapper.eq("player_id", player.getId()).eq("status", "PRESENT");
                presentCount = attendanceMapper.selectCount(wrapper).intValue();
            }

            // 计算出勤率
            double attendanceRate = seasonMatchCount > 0
                ? Math.round((double) presentCount / seasonMatchCount * 10000) / 100.0
                : 0.0;

            stat.put("seasonMatchCount", seasonMatchCount);
            stat.put("presentCount", presentCount);
            stat.put("attendanceRate", attendanceRate);

            result.add(stat);
        }

        // 按出勤率降序排序
        result.sort((a, b) -> Double.compare((Double) b.get("attendanceRate"), (Double) a.get("attendanceRate")));

        // 添加排名
        for (int i = 0; i < result.size(); i++) {
            result.get(i).put("rank", i + 1);
        }

        return result;
    }
}
