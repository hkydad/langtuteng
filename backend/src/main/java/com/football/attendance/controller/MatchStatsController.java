package com.football.attendance.controller;

import com.football.attendance.entity.*;
import com.football.attendance.service.MatchStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/match-stats")
@CrossOrigin
public class MatchStatsController {

    @Autowired
    private MatchStatsService matchStatsService;

    @PostMapping("/quarter")
    public QuarterStat saveQuarter(@RequestBody QuarterStat quarter) {
        return matchStatsService.saveQuarter(quarter);
    }

    @GetMapping("/quarter/match/{matchId}")
    public List<QuarterStat> getQuartersByMatchId(@PathVariable Long matchId) {
        return matchStatsService.getQuartersByMatchId(matchId);
    }

    @DeleteMapping("/quarter/{id}")
    public void deleteQuarter(@PathVariable Long id) {
        matchStatsService.deleteQuarter(id);
    }

    @PostMapping("/goal")
    public void saveGoal(@RequestBody GoalRecord goal) {
        matchStatsService.saveGoal(goal);
    }

    @GetMapping("/goal/quarter/{quarterId}")
    public List<GoalRecord> getGoalsByQuarterId(@PathVariable Long quarterId) {
        return matchStatsService.getGoalsByQuarterId(quarterId);
    }

    @DeleteMapping("/goal/{id}")
    public void deleteGoal(@PathVariable Long id) {
        matchStatsService.deleteGoal(id);
    }

    @PostMapping("/assist")
    public void saveAssist(@RequestBody AssistRecord assist) {
        matchStatsService.saveAssist(assist);
    }

    @GetMapping("/assist/quarter/{quarterId}")
    public List<AssistRecord> getAssistsByQuarterId(@PathVariable Long quarterId) {
        return matchStatsService.getAssistsByQuarterId(quarterId);
    }

    @DeleteMapping("/assist/{id}")
    public void deleteAssist(@PathVariable Long id) {
        matchStatsService.deleteAssist(id);
    }

    @PostMapping("/goalkeeper")
    public void saveGoalkeeper(@RequestBody GoalkeeperRecord goalkeeper) {
        matchStatsService.saveGoalkeeper(goalkeeper);
    }

    @GetMapping("/goalkeeper/quarter/{quarterId}")
    public List<GoalkeeperRecord> getGoalkeepersByQuarterId(@PathVariable Long quarterId) {
        return matchStatsService.getGoalkeepersByQuarterId(quarterId);
    }

    @DeleteMapping("/goalkeeper/{id}")
    public void deleteGoalkeeper(@PathVariable Long id) {
        matchStatsService.deleteGoalkeeper(id);
    }

    @GetMapping("/leaderboard/goals")
    public List<Map<String, Object>> getGoalLeaderboard(@RequestParam(required = false) Integer season) {
        return matchStatsService.getGoalLeaderboard(season);
    }

    @GetMapping("/leaderboard/assists")
    public List<Map<String, Object>> getAssistLeaderboard(@RequestParam(required = false) Integer season) {
        return matchStatsService.getAssistLeaderboard(season);
    }

    @GetMapping("/leaderboard/goalkeepers")
    public List<Map<String, Object>> getGoalkeeperLeaderboard(@RequestParam(required = false) Integer season) {
        return matchStatsService.getGoalkeeperLeaderboard(season);
    }

    @GetMapping("/leaderboard/team-standings")
    public List<Map<String, Object>> getTeamStandingLeaderboard(@RequestParam(required = false) Integer season) {
        return matchStatsService.getTeamStandingLeaderboard(season);
    }
}
