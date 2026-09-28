package com.leaguetrack.service;

import com.leaguetrack.entity.Fixture;
import com.leaguetrack.entity.Team;
import com.leaguetrack.repository.FixtureRepository;
import com.leaguetrack.repository.TeamRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FixtureService {

    private final FixtureRepository fixtureRepository;
    private final TeamRepository teamRepository;

    public FixtureService(FixtureRepository fixtureRepository,
                          TeamRepository teamRepository) {
        this.fixtureRepository = fixtureRepository;
        this.teamRepository = teamRepository;
    }

    public List<Fixture> generateFixtures() {

        List<Team> teams = teamRepository.findAll();

        if (teams.size() < 2) {
            throw new IllegalArgumentException(
                    "At least 2 teams are required to generate fixtures"
            );
        }

        List<Fixture> existingFixtures =
                fixtureRepository.findAll();

        List<Fixture> newFixtures =
                new ArrayList<>();

        /*
         * Check every possible team pair.
         * Create a fixture only if that pair
         * does not already have a fixture.
         */
        for (int i = 0; i < teams.size(); i++) {

            for (int j = i + 1; j < teams.size(); j++) {

                Team homeTeam = teams.get(i);
                Team awayTeam = teams.get(j);

                boolean alreadyExists =
                        existingFixtures.stream()
                                .anyMatch(fixture ->
                                        (fixture.getHomeTeam().getId()
                                                .equals(homeTeam.getId())
                                                &&
                                                fixture.getAwayTeam().getId()
                                                        .equals(awayTeam.getId()))
                                                ||
                                                (fixture.getHomeTeam().getId()
                                                        .equals(awayTeam.getId())
                                                        &&
                                                        fixture.getAwayTeam().getId()
                                                                .equals(homeTeam.getId()))
                                );

                if (!alreadyExists) {

                    Fixture fixture =
                            new Fixture(
                                    homeTeam,
                                    awayTeam
                            );

                    newFixtures.add(fixture);
                }
            }
        }

        if (newFixtures.isEmpty()) {

            throw new IllegalArgumentException(
                    "All possible fixtures have already been generated"
            );
        }

        return fixtureRepository.saveAll(newFixtures);
    }


    public List<Fixture> getAllFixtures() {

        return fixtureRepository.findAll();
    }


    public Fixture getFixtureById(Long id) {

        return fixtureRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Fixture not found with id: " + id
                        )
                );
    }
}