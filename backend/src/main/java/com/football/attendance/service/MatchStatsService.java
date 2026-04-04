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

    public List<Map<String, Object>> getGoalLeaderboard(Integer season) {
        String sql = season != null
            ? "SELECT g.player_id, g.player_name, g.team_name, COUNT(*) as goal_count FROM match_goal g " +
              "LEFT JOIN match_quarter q ON g.quarter_id = q.id " +
              "LEFT JOIN football_match m ON q.match_id = m.id " +
              "WHERE m.season = ? OR m.season IS NULL " +
              "GROUP BY g.player_id, g.player_name, g.team_name ORDER BY goal_count DESC"
            : "SELECT player_id, player_name, team_name, COUNT(*) as goal_count FROM match_goal GROUP BY player_id, player_name, team_name ORDER BY goal_count DESC";

        if (season != null) {
            return jdbcTemplate.query(sql,
                (rs, rowNum) -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("playerId", rs.getLong("player_id"));
                    item.put("playerName", rs.getString("player_name"));
                    item.put("teamName", rs.getString("team_name"));
                    item.put("goalCount", rs.getInt("goal_count"));
                    return item;
                }, season);
        } else {
            return jdbcTemplate.query(sql,
                (rs, rowNum) -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("playerId", rs.getLong("player_id"));
                    item.put("playerName", rs.getString("player_name"));
                    item.put("teamName", rs.getString("team_name"));
                    item.put("goalCount", rs.getInt("goal_count"));
                    return item;
                });
        }
    }

    public List<Map<String, Object>> getAssistLeaderboard(Integer season) {
        String sql = season != null
            ? "SELECT a.player_id, a.player_name, a.team_name, COUNT(*) as assist_count FROM match_assist a " +
              "LEFT JOIN match_quarter q ON a.quarter_id = q.id " +
              "LEFT JOIN football_match m ON q.match_id = m.id " +
              "WHERE m.season = ? OR m.season IS NULL " +
              "GROUP BY a.player_id, a.player_name, a.team_name ORDER BY assist_count DESC"
            : "SELECT player_id, player_name, team_name, COUNT(*) as assist_count FROM match_assist GROUP BY player_id, player_name, team_name ORDER BY assist_count DESC";

        if (season != null) {
            return jdbcTemplate.query(sql,
                (rs, rowNum) -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("playerId", rs.getLong("player_id"));
                    item.put("playerName", rs.getString("player_name"));
                    item.put("teamName", rs.getString("team_name"));
                    item.put("assistCount", rs.getInt("assist_count"));
                    return item;
                }, season);
        } else {
            return jdbcTemplate.query(sql,
                (rs, rowNum) -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("playerId", rs.getLong("player_id"));
                    item.put("playerName", rs.getString("player_name"));
                    item.put("teamName", rs.getString("team_name"));
                    item.put("assistCount", rs.getInt("assist_count"));
                    return item;
                });
        }
    }

    public List<Map<String, Object>> getGoalkeeperLeaderboard(Integer season) {
        String baseSql = season != null
            ? "SELECT gk.id, gk.quarter_id, gk.player_id, gk.player_name, gk.team_name, q.match_id, m.season " +
              "FROM match_goalkeeper gk LEFT JOIN match_quarter q ON gk.quarter_id = q.id " +
              "LEFT JOIN football_match m ON q.match_id = m.id WHERE m.season = ?"
            : "SELECT gk.id, gk.quarter_id, gk.player_id, gk.player_name, gk.team_name, q.match_id, m.season " +
              "FROM match_goalkeeper gk LEFT JOIN match_quarter q ON gk.quarter_id = q.id " +
              "LEFT JOIN football_match m ON q.match_id = m.id";

        List<GoalkeeperRecord> allGoalkeepers = season != null
            ? jdbcTemplate.query(baseSql,
                (rs, rowNum) -> {
                    GoalkeeperRecord gk = new GoalkeeperRecord();
                    gk.setId(rs.getLong("id"));
                    gk.setQuarterId(rs.getLong("quarter_id"));
                    gk.setPlayerId(rs.getLong("player_id"));
                    gk.setPlayerName(rs.getString("player_name"));
                    gk.setTeamName(rs.getString("team_name"));
                    return gk;
                }, season)
            : jdbcTemplate.query(baseSql,
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

            boolean isCleanSheet = checkCleanSheet(gk.getQuarterId(), teamName);
            if (isCleanSheet) {
                int cleanSheets = ((Number) stat.get("cleanSheetCount")).intValue();
                stat.put("totalScore", ((Number) stat.get("totalScore")).intValue() + 3);
                stat.put("cleanSheetCount", cleanSheets + 1);
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

    private boolean checkCleanSheet(Long quarterId, String teamName) {
        if (teamName == null || teamName.trim().isEmpty()) return false;
        String trimmedTeamName = teamName.trim();
        List<QuarterStat> quarters = jdbcTemplate.query(
            "SELECT * FROM match_quarter WHERE id = ?",
            (rs, rowNum) -> {
                QuarterStat q = new QuarterStat();
                q.setId(rs.getLong("id"));
                q.setMatchId(rs.getLong("match_id"));
                q.setTeam1Name(rs.getString("team1_name"));
                q.setTeam2Name(rs.getString("team2_name"));
                q.setTeam1Score(rs.getInt("team1_score"));
                q.setTeam2Score(rs.getInt("team2_score"));
                return q;
            }, quarterId);

        if (quarters.isEmpty()) return false;
        QuarterStat q = quarters.get(0);
        String t1 = q.getTeam1Name() != null ? q.getTeam1Name().trim() : "";
        String t2 = q.getTeam2Name() != null ? q.getTeam2Name().trim() : "";
        if (t1.equals(trimmedTeamName)) {
            return q.getTeam2Score() != null && q.getTeam2Score() == 0;
        } else if (t2.equals(trimmedTeamName)) {
            return q.getTeam1Score() != null && q.getTeam1Score() == 0;
        }
        return false;
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

    public List<Map<String, Object>> getTeamStandingLeaderboard(Integer season) {
        String sql = season != null
            ? "SELECT q.team1_name as team_name, q.team1_score, q.team2_score FROM match_quarter q " +
              "LEFT JOIN football_match m ON q.match_id = m.id WHERE m.season = ?"
            : "SELECT q.team1_name as team_name, q.team1_score, q.team2_score FROM match_quarter q";

        List<QuarterStat> quarters = season != null
            ? jdbcTemplate.query(sql,
                (rs, rowNum) -> {
                    QuarterStat q = new QuarterStat();
                    q.setTeam1Name(rs.getString("team_name"));
                    q.setTeam1Score(rs.getInt("team1_score"));
                    q.setTeam2Score(rs.getInt("team2_score"));
                    return q;
                }, season)
            : jdbcTemplate.query(sql,
                (rs, rowNum) -> {
                    QuarterStat q = new QuarterStat();
                    q.setTeam1Name(rs.getString("team_name"));
                    q.setTeam1Score(rs.getInt("team1_score"));
                    q.setTeam2Score(rs.getInt("team2_score"));
                    return q;
                });

        // Also need team2 rows
        String sql2 = season != null
            ? "SELECT q.team2_name as team_name, q.team2_score, q.team1_score FROM match_quarter q " +
              "LEFT JOIN football_match m ON q.match_id = m.id WHERE m.season = ?"
            : "SELECT q.team2_name as team_name, q.team2_score, q.team1_score FROM match_quarter q";

        List<QuarterStat> team2Quarters = season != null
            ? jdbcTemplate.query(sql2,
                (rs, rowNum) -> {
                    QuarterStat q = new QuarterStat();
                    q.setTeam1Name(rs.getString("team_name"));
                    q.setTeam1Score(rs.getInt("team2_score"));
                    q.setTeam2Score(rs.getInt("team1_score"));
                    return q;
                }, season)
            : jdbcTemplate.query(sql2,
                (rs, rowNum) -> {
                    QuarterStat q = new QuarterStat();
                    q.setTeam1Name(rs.getString("team_name"));
                    q.setTeam1Score(rs.getInt("team2_score"));
                    q.setTeam2Score(rs.getInt("team1_score"));
                    return q;
                });

        // Aggregate
        Map<String, Map<String, Integer>> teamStats = new LinkedHashMap<>();
        for (QuarterStat q : quarters) {
            aggregateTeam(q.getTeam1Name(), q.getTeam1Score(), q.getTeam2Score(), teamStats);
        }
        for (QuarterStat q : team2Quarters) {
            aggregateTeam(q.getTeam1Name(), q.getTeam1Score(), q.getTeam2Score(), teamStats);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Map<String, Integer>> entry : teamStats.entrySet()) {
            Map<String, Object> row = new HashMap<>();
            row.put("teamName", entry.getKey());
            int points = entry.getValue().get("points");
            int goalsScored = entry.getValue().get("goalsScored");
            int goalsConceded = entry.getValue().get("goalsConceded");
            row.put("points", points);
            row.put("goalsScored", goalsScored);
            row.put("goalsConceded", goalsConceded);
            row.put("goalDifference", goalsScored - goalsConceded);
            result.add(row);
        }

        // Sort: points DESC, goalDifference DESC, goalsScored DESC
        result.sort((a, b) -> {
            int cmp = ((Number) b.get("points")).intValue() - ((Number) a.get("points")).intValue();
            if (cmp != 0) return cmp;
            cmp = ((Number) b.get("goalDifference")).intValue() - ((Number) a.get("goalDifference")).intValue();
            if (cmp != 0) return cmp;
            return ((Number) b.get("goalsScored")).intValue() - ((Number) a.get("goalsScored")).intValue();
        });

        // Assign ranks (same rank for ties)
        int rank = 1;
        for (int i = 0; i < result.size(); i++) {
            if (i > 0) {
                Map<String, Object> prev = result.get(i - 1);
                Map<String, Object> curr = result.get(i);
                int prevPoints = ((Number) prev.get("points")).intValue();
                int prevGd = ((Number) prev.get("goalDifference")).intValue();
                int prevGs = ((Number) prev.get("goalsScored")).intValue();
                int currPoints = ((Number) curr.get("points")).intValue();
                int currGd = ((Number) curr.get("goalDifference")).intValue();
                int currGs = ((Number) curr.get("goalsScored")).intValue();
                if (currPoints == prevPoints && currGd == prevGd && currGs == prevGs) {
                    // same rank
                } else {
                    rank = i + 1;
                }
            }
            result.get(i).put("rank", rank);
        }

        return result;
    }

    private void aggregateTeam(String teamName, int scored, int conceded, Map<String, Map<String, Integer>> teamStats) {
        if (teamName == null || teamName.isEmpty()) return;
        if (!teamStats.containsKey(teamName)) {
            Map<String, Integer> stats = new HashMap<>();
            stats.put("points", 0);
            stats.put("goalsScored", 0);
            stats.put("goalsConceded", 0);
            teamStats.put(teamName, stats);
        }
        Map<String, Integer> stats = teamStats.get(teamName);
        stats.put("goalsScored", stats.get("goalsScored") + scored);
        stats.put("goalsConceded", stats.get("goalsConceded") + conceded);
        if (scored > conceded) {
            stats.put("points", stats.get("points") + 3);
        } else if (scored == conceded) {
            stats.put("points", stats.get("points") + 1);
        }
    }
}
