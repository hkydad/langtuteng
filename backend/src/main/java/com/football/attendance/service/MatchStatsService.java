package com.football.attendance.service;

import com.football.attendance.entity.*;
import com.football.attendance.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MatchStatsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private QuarterStatMapper quarterStatMapper;

    @Autowired
    private GoalRecordMapper goalRecordMapper;

    @Autowired
    private AssistRecordMapper assistRecordMapper;

    @Autowired
    private GoalkeeperRecordMapper goalkeeperRecordMapper;

    public QuarterStat saveQuarter(QuarterStat quarter) {
        if (quarter.getId() == null) {
            Number maxIdNum = jdbcTemplate.queryForObject("SELECT MAX(id) FROM match_quarter", Number.class);
            quarter.setId(maxIdNum == null ? 1 : maxIdNum.longValue() + 1);
            jdbcTemplate.update("INSERT INTO match_quarter (id, match_id, quarter_num, team1_name, team2_name, team1_score, team2_score, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                quarter.getId(), quarter.getMatchId(), quarter.getQuarterNum(), quarter.getTeam1Name(), quarter.getTeam2Name(),
                quarter.getTeam1Score() != null ? quarter.getTeam1Score() : 0, quarter.getTeam2Score() != null ? quarter.getTeam2Score() : 0, LocalDateTime.now());
        } else {
            jdbcTemplate.update("UPDATE match_quarter SET match_id = ?, quarter_num = ?, team1_name = ?, team2_name = ?, team1_score = ?, team2_score = ? WHERE id = ?",
                quarter.getMatchId(), quarter.getQuarterNum(), quarter.getTeam1Name(), quarter.getTeam2Name(), quarter.getTeam1Score(), quarter.getTeam2Score(), quarter.getId());
        }
        return quarter;
    }

    public List<QuarterStat> getQuartersByMatchId(Long matchId) {
        return jdbcTemplate.query("SELECT * FROM match_quarter WHERE match_id = ? ORDER BY quarter_num",
            (rs, rowNum) -> {
                QuarterStat q = new QuarterStat();
                q.setId(rs.getLong("id"));
                q.setMatchId(rs.getLong("match_id"));
                q.setQuarterNum(rs.getInt("quarter_num"));
                q.setTeam1Name(rs.getString("team1_name"));
                q.setTeam2Name(rs.getString("team2_name"));
                q.setTeam1Score(rs.getInt("team1_score"));
                q.setTeam2Score(rs.getInt("team2_score"));
                return q;
            }, matchId);
    }

    public void deleteQuarter(Long id) {
        jdbcTemplate.update("DELETE FROM match_goal WHERE quarter_id = ?", id);
        jdbcTemplate.update("DELETE FROM match_assist WHERE quarter_id = ?", id);
        jdbcTemplate.update("DELETE FROM match_goalkeeper WHERE quarter_id = ?", id);
        jdbcTemplate.update("DELETE FROM match_quarter WHERE id = ?", id);
    }

    public void saveGoal(GoalRecord goal) {
        Number maxIdNum = jdbcTemplate.queryForObject("SELECT MAX(id) FROM match_goal", Number.class);
        goal.setId(maxIdNum == null ? 1 : maxIdNum.longValue() + 1);
        jdbcTemplate.update("INSERT INTO match_goal (id, quarter_id, player_id, player_name, team_name, created_at) VALUES (?, ?, ?, ?, ?, ?)",
            goal.getId(), goal.getQuarterId(), goal.getPlayerId(), goal.getPlayerName(), goal.getTeamName(), LocalDateTime.now());
    }

    public void saveAssist(AssistRecord assist) {
        Number maxIdNum = jdbcTemplate.queryForObject("SELECT MAX(id) FROM match_assist", Number.class);
        assist.setId(maxIdNum == null ? 1 : maxIdNum.longValue() + 1);
        jdbcTemplate.update("INSERT INTO match_assist (id, quarter_id, player_id, player_name, team_name, created_at) VALUES (?, ?, ?, ?, ?, ?)",
            assist.getId(), assist.getQuarterId(), assist.getPlayerId(), assist.getPlayerName(), assist.getTeamName(), LocalDateTime.now());
    }

    public void saveGoalkeeper(GoalkeeperRecord goalkeeper) {
        Number maxIdNum = jdbcTemplate.queryForObject("SELECT MAX(id) FROM match_goalkeeper", Number.class);
        goalkeeper.setId(maxIdNum == null ? 1 : maxIdNum.longValue() + 1);
        jdbcTemplate.update("INSERT INTO match_goalkeeper (id, quarter_id, player_id, player_name, team_name, created_at) VALUES (?, ?, ?, ?, ?, ?)",
            goalkeeper.getId(), goalkeeper.getQuarterId(), goalkeeper.getPlayerId(), goalkeeper.getPlayerName(), goalkeeper.getTeamName(), LocalDateTime.now());
    }

    public List<GoalRecord> getGoalsByQuarterId(Long quarterId) {
        return jdbcTemplate.query("SELECT * FROM match_goal WHERE quarter_id = ?",
            (rs, rowNum) -> {
                GoalRecord g = new GoalRecord();
                g.setId(rs.getLong("id"));
                g.setQuarterId(rs.getLong("quarter_id"));
                g.setPlayerId(rs.getLong("player_id"));
                g.setPlayerName(rs.getString("player_name"));
                g.setTeamName(rs.getString("team_name"));
                return g;
            }, quarterId);
    }

    public List<AssistRecord> getAssistsByQuarterId(Long quarterId) {
        return jdbcTemplate.query("SELECT * FROM match_assist WHERE quarter_id = ?",
            (rs, rowNum) -> {
                AssistRecord a = new AssistRecord();
                a.setId(rs.getLong("id"));
                a.setQuarterId(rs.getLong("quarter_id"));
                a.setPlayerId(rs.getLong("player_id"));
                a.setPlayerName(rs.getString("player_name"));
                a.setTeamName(rs.getString("team_name"));
                return a;
            }, quarterId);
    }

    public List<GoalkeeperRecord> getGoalkeepersByQuarterId(Long quarterId) {
        return jdbcTemplate.query("SELECT * FROM match_goalkeeper WHERE quarter_id = ?",
            (rs, rowNum) -> {
                GoalkeeperRecord g = new GoalkeeperRecord();
                g.setId(rs.getLong("id"));
                g.setQuarterId(rs.getLong("quarter_id"));
                g.setPlayerId(rs.getLong("player_id"));
                g.setPlayerName(rs.getString("player_name"));
                g.setTeamName(rs.getString("team_name"));
                return g;
            }, quarterId);
    }

    public List<Map<String, Object>> getGoalLeaderboard() {
        return jdbcTemplate.query("SELECT player_id, player_name, team_name, COUNT(*) as goal_count FROM match_goal GROUP BY player_id, player_name, team_name ORDER BY goal_count DESC",
            (rs, rowNum) -> {
                Map<String, Object> item = new HashMap<>();
                item.put("playerId", rs.getLong("player_id"));
                item.put("playerName", rs.getString("player_name"));
                item.put("teamName", rs.getString("team_name"));
                item.put("goalCount", rs.getInt("goal_count"));
                return item;
            });
    }

    public List<Map<String, Object>> getAssistLeaderboard() {
        return jdbcTemplate.query("SELECT player_id, player_name, team_name, COUNT(*) as assist_count FROM match_assist GROUP BY player_id, player_name, team_name ORDER BY assist_count DESC",
            (rs, rowNum) -> {
                Map<String, Object> item = new HashMap<>();
                item.put("playerId", rs.getLong("player_id"));
                item.put("playerName", rs.getString("player_name"));
                item.put("teamName", rs.getString("team_name"));
                item.put("assistCount", rs.getInt("assist_count"));
                return item;
            });
    }

    public List<Map<String, Object>> getGoalkeeperLeaderboard() {
        List<GoalkeeperRecord> allGoalkeepers = jdbcTemplate.query(
            "SELECT gk.id, gk.quarter_id, gk.player_id, gk.player_name, gk.team_name, q.match_id " +
            "FROM match_goalkeeper gk LEFT JOIN match_quarter q ON gk.quarter_id = q.id",
            (rs, rowNum) -> {
                GoalkeeperRecord gk = new GoalkeeperRecord();
                gk.setId(rs.getLong("id"));
                gk.setQuarterId(rs.getLong("quarter_id"));
                gk.setPlayerId(rs.getLong("player_id"));
                gk.setPlayerName(rs.getString("player_name"));
                gk.setTeamName(rs.getString("team_name"));
                return gk;
            });

        Map<Long, Map<String, Object>> playerStats = new LinkedHashMap<>();

        for (GoalkeeperRecord gk : allGoalkeepers) {
            Long playerId = gk.getPlayerId();
            String playerName = gk.getPlayerName();
            String teamName = gk.getTeamName();

            if (!playerStats.containsKey(playerId)) {
                Map<String, Object> stat = new HashMap<>();
                stat.put("playerId", playerId);
                stat.put("playerName", playerName);
                stat.put("teamName", teamName);
                stat.put("totalScore", 0);
                stat.put("cleanSheetCount", 0);
                playerStats.put(playerId, stat);
            }

            Map<String, Object> stat = playerStats.get(playerId);
            int currentScore = ((Number) stat.get("totalScore")).intValue();
            stat.put("totalScore", currentScore + 1);

            Long matchId = jdbcTemplate.queryForObject(
                "SELECT match_id FROM match_quarter WHERE id = ?", Long.class, gk.getQuarterId());

            if (matchId != null) {
                boolean isCleanSheet = checkCleanSheet(matchId, teamName);
                if (isCleanSheet) {
                    int cleanSheets = ((Number) stat.get("cleanSheetCount")).intValue();
                    if (cleanSheets == 0) {
                        stat.put("totalScore", ((Number) stat.get("totalScore")).intValue() + 3);
                    }
                    stat.put("cleanSheetCount", cleanSheets + 1);
                }
            }
        }

        List<Map<String, Object>> result = new ArrayList<>(playerStats.values());
        result.sort((a, b) -> {
            int scoreA = ((Number) a.get("totalScore")).intValue();
            int scoreB = ((Number) b.get("totalScore")).intValue();
            return Integer.compare(scoreB, scoreA);
        });

        return result;
    }

    private boolean checkCleanSheet(Long matchId, String teamName) {
        List<QuarterStat> quarters = jdbcTemplate.query(
            "SELECT * FROM match_quarter WHERE match_id = ?",
            (rs, rowNum) -> {
                QuarterStat q = new QuarterStat();
                q.setId(rs.getLong("id"));
                q.setMatchId(rs.getLong("match_id"));
                q.setTeam1Name(rs.getString("team1_name"));
                q.setTeam2Name(rs.getString("team2_name"));
                q.setTeam1Score(rs.getInt("team1_score"));
                q.setTeam2Score(rs.getInt("team2_score"));
                return q;
            }, matchId);

        int totalGoalsConceded = 0;
        for (QuarterStat q : quarters) {
            if (q.getTeam1Name().equals(teamName)) {
                totalGoalsConceded += q.getTeam2Score();
            } else if (q.getTeam2Name().equals(teamName)) {
                totalGoalsConceded += q.getTeam1Score();
            }
        }

        return totalGoalsConceded == 0;
    }

    public void deleteGoal(Long id) {
        jdbcTemplate.update("DELETE FROM match_goal WHERE id = ?", id);
    }

    public void deleteAssist(Long id) {
        jdbcTemplate.update("DELETE FROM match_assist WHERE id = ?", id);
    }

    public void deleteGoalkeeper(Long id) {
        jdbcTemplate.update("DELETE FROM match_goalkeeper WHERE id = ?", id);
    }
}
