package com.football.attendance.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DbInitRunner implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            jdbcTemplate.execute("ALTER TABLE player ADD COLUMN member_level INTEGER");
        } catch (Exception e) {
            // 字段已存在，忽略
        }
        try {
            jdbcTemplate.execute("ALTER TABLE player ADD COLUMN team_name TEXT");
        } catch (Exception e) {
            // 字段已存在，忽略
        }
        try {
            jdbcTemplate.execute("ALTER TABLE player ADD COLUMN season INTEGER");
        } catch (Exception e) {
            // 字段已存在，忽略
        }
        try {
            jdbcTemplate.execute("ALTER TABLE team ADD COLUMN season INTEGER");
        } catch (Exception e) {
            // 字段已存在，忽略
        }
        try {
            jdbcTemplate.execute("ALTER TABLE football_match ADD COLUMN season INTEGER");
        } catch (Exception e) {
            // 字段已存在，忽略
        }
        try {
            jdbcTemplate.execute("UPDATE football_match SET season = strftime('%Y', match_date) WHERE season IS NULL");
        } catch (Exception e) {
            // 忽略
        }
        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS match_quarter (id INTEGER PRIMARY KEY AUTOINCREMENT, match_id INTEGER NOT NULL, quarter_num INTEGER NOT NULL, team1_name TEXT NOT NULL, team2_name TEXT NOT NULL, team1_score INTEGER DEFAULT 0, team2_score INTEGER DEFAULT 0, created_at TEXT DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (match_id) REFERENCES football_match(id))");
        } catch (Exception e) {
            // 表已存在，忽略
        }
        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS match_goal (id INTEGER PRIMARY KEY AUTOINCREMENT, quarter_id INTEGER NOT NULL, player_id INTEGER NOT NULL, player_name TEXT NOT NULL, team_name TEXT NOT NULL, created_at TEXT DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (quarter_id) REFERENCES match_quarter(id), FOREIGN KEY (player_id) REFERENCES player(id))");
        } catch (Exception e) {
            // 表已存在，忽略
        }
        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS match_assist (id INTEGER PRIMARY KEY AUTOINCREMENT, quarter_id INTEGER NOT NULL, player_id INTEGER NOT NULL, player_name TEXT NOT NULL, team_name TEXT NOT NULL, created_at TEXT DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (quarter_id) REFERENCES match_quarter(id), FOREIGN KEY (player_id) REFERENCES player(id))");
        } catch (Exception e) {
            // 表已存在，忽略
        }
        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS match_goalkeeper (id INTEGER PRIMARY KEY AUTOINCREMENT, quarter_id INTEGER NOT NULL, player_id INTEGER NOT NULL, player_name TEXT NOT NULL, team_name TEXT NOT NULL, created_at TEXT DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (quarter_id) REFERENCES match_quarter(id), FOREIGN KEY (player_id) REFERENCES player(id))");
        } catch (Exception e) {
            // 表已存在，忽略
        }
        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS team (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, created_at TEXT DEFAULT CURRENT_TIMESTAMP)");
        } catch (Exception e) {
            // 表已存在，忽略
        }
        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS season (id INTEGER PRIMARY KEY AUTOINCREMENT, year INTEGER NOT NULL, name TEXT, created_at TEXT DEFAULT CURRENT_TIMESTAMP)");
        } catch (Exception e) {
            // 表已存在，忽略
        }
        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS venue (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, address TEXT, created_at TEXT DEFAULT CURRENT_TIMESTAMP)");
        } catch (Exception e) {
            // 表已存在，忽略
        }
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM season", Integer.class);
            if (count != null && count == 0) {
                jdbcTemplate.update("INSERT INTO season (id, year, name, created_at) VALUES (?, ?, ?, ?)",
                    1, 2026, "2026赛季", LocalDateTime.now());
            }
        } catch (Exception e) {
            // 忽略
        }
    }
}
