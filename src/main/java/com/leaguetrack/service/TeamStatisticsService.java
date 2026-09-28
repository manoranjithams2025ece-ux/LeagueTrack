package com.leaguetrack.service;

import com.leaguetrack.entity.StandingsEntry;
import com.leaguetrack.entity.Team;
import com.leaguetrack.repository.StandingsRepository;
import com.leaguetrack.repository.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TeamStatisticsService {

    private final TeamRepository teamRepository;
    private final StandingsRepository standingsRepository;

    public TeamStatisticsService(TeamRepository teamRepository,
                                 StandingsRepository standingsRepository) {
        this.teamRepository = teamRepository;
        this.standingsRepository = standingsRepository;
    }

    public Map<String, Object> getTeamStatistics(Long teamId) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Team not found with id: " + teamId
                        ));

        StandingsEntry standing = standingsRepository
                .findByTeamId(teamId)
                .orElseGet(() -> new StandingsEntry(team));

        Map<String, Object> statistics = new HashMap<>();

        statistics.put("teamId", team.getId());
        statistics.put("teamName", team.getName());
        statistics.put("played", standing.getPlayed());
        statistics.put("wins", standing.getWins());
        statistics.put("draws", standing.getDraws());
        statistics.put("losses", standing.getLosses());
        statistics.put("goalsScored", standing.getGoalsScored());
        statistics.put("goalsConceded", standing.getGoalsConceded());
        statistics.put("points", standing.getPoints());

        return statistics;
    }
}