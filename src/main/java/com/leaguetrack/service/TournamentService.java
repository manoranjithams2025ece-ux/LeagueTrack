package com.leaguetrack.service;

import com.leaguetrack.entity.Fixture;
import com.leaguetrack.entity.StandingsEntry;
import com.leaguetrack.repository.FixtureRepository;
import com.leaguetrack.repository.StandingsRepository;
import com.leaguetrack.repository.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TournamentService {

    private final TeamRepository teamRepository;
    private final FixtureRepository fixtureRepository;
    private final StandingsRepository standingsRepository;

    public TournamentService(TeamRepository teamRepository,
                             FixtureRepository fixtureRepository,
                             StandingsRepository standingsRepository) {
        this.teamRepository = teamRepository;
        this.fixtureRepository = fixtureRepository;
        this.standingsRepository = standingsRepository;
    }

    public Map<String, Object> getTournamentSummary() {

        List<Fixture> fixtures = fixtureRepository.findAll();
        List<StandingsEntry> standings = standingsRepository.findAll();

        int totalFixtures = fixtures.size();

        long completedMatches = fixtures.stream()
                .filter(Fixture::isCompleted)
                .count();

        long pendingMatches = totalFixtures - completedMatches;

        int totalGoals = standings.stream()
                .mapToInt(StandingsEntry::getGoalsScored)
                .sum();

        String leader = standings.stream()
                .max((a, b) ->
                        Integer.compare(a.getPoints(), b.getPoints()))
                .map(entry -> entry.getTeam().getName())
                .orElse("No leader yet");

        Map<String, Object> summary = new HashMap<>();

        summary.put("totalTeams", teamRepository.count());
        summary.put("totalFixtures", totalFixtures);
        summary.put("completedMatches", completedMatches);
        summary.put("pendingMatches", pendingMatches);
        summary.put("totalGoals", totalGoals);
        summary.put("currentLeader", leader);

        return summary;
    }
}