package com.leaguetrack.controller;

import com.leaguetrack.service.TeamStatisticsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/teams")
public class TeamStatisticsController {

    private final TeamStatisticsService teamStatisticsService;

    public TeamStatisticsController(
            TeamStatisticsService teamStatisticsService) {
        this.teamStatisticsService = teamStatisticsService;
    }

    @GetMapping("/{teamId}/statistics")
    public Map<String, Object> getTeamStatistics(
            @PathVariable Long teamId) {

        return teamStatisticsService.getTeamStatistics(teamId);
    }
}