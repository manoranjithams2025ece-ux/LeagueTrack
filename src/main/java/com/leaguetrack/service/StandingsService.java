package com.leaguetrack.service;

import com.leaguetrack.entity.StandingsEntry;
import com.leaguetrack.entity.Team;
import com.leaguetrack.repository.StandingsRepository;
import com.leaguetrack.repository.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class StandingsService {

    private final StandingsRepository standingsRepository;
    private final TeamRepository teamRepository;

    public StandingsService(StandingsRepository standingsRepository,
                            TeamRepository teamRepository) {
        this.standingsRepository = standingsRepository;
        this.teamRepository = teamRepository;
    }

    public List<StandingsEntry> getStandings() {

        List<Team> teams = teamRepository.findAll();

        for (Team team : teams) {
            if (standingsRepository.findByTeamId(team.getId()).isEmpty()) {
                standingsRepository.save(new StandingsEntry(team));
            }
        }

        return standingsRepository.findAll()
                .stream()
                .sorted(Comparator.comparingInt(StandingsEntry::getPoints).reversed())
                .toList();
    }
}