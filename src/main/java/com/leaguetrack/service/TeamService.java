package com.leaguetrack.service;

import com.leaguetrack.entity.Team;
import com.leaguetrack.repository.StandingsRepository;
import com.leaguetrack.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final StandingsRepository standingsRepository;

    public TeamService(TeamRepository teamRepository,
                       StandingsRepository standingsRepository) {
        this.teamRepository = teamRepository;
        this.standingsRepository = standingsRepository;
    }

    public Team registerTeam(Team team) {

        if (teamRepository.findByNameIgnoreCase(team.getName()).isPresent()) {
            throw new IllegalArgumentException(
                    "Team with name '" + team.getName() + "' already exists"
            );
        }

        return teamRepository.save(team);
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Team getTeamById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Team not found with id: " + id
                        )
                );
    }

    @Transactional
    public void deleteTeam(Long id) {

        if (!teamRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Team not found with id: " + id
            );
        }

        // Delete the team's standings entry first
        standingsRepository.deleteByTeamId(id);

        // Then delete the team
        teamRepository.deleteById(id);
    }
}