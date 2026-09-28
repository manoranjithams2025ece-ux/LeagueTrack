package com.leaguetrack.service;

import com.leaguetrack.dto.MatchResultRequest;
import com.leaguetrack.entity.Fixture;
import com.leaguetrack.entity.Match;
import com.leaguetrack.entity.StandingsEntry;
import com.leaguetrack.entity.Team;
import com.leaguetrack.repository.FixtureRepository;
import com.leaguetrack.repository.MatchRepository;
import com.leaguetrack.repository.StandingsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final FixtureRepository fixtureRepository;
    private final StandingsRepository standingsRepository;

    @Value("${league.scoring.win:3}")
    private int winPoints;

    @Value("${league.scoring.draw:1}")
    private int drawPoints;

    @Value("${league.scoring.loss:0}")
    private int lossPoints;

    public MatchService(MatchRepository matchRepository,
                        FixtureRepository fixtureRepository,
                        StandingsRepository standingsRepository) {
        this.matchRepository = matchRepository;
        this.fixtureRepository = fixtureRepository;
        this.standingsRepository = standingsRepository;
    }

    public Match createMatch(Long fixtureId) {

        Fixture fixture = fixtureRepository.findById(fixtureId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Fixture not found for id: " + fixtureId
                        ));

        if (matchRepository.findByFixtureId(fixtureId).isPresent()) {
            throw new IllegalArgumentException(
                    "Match already exists for fixture: " + fixtureId
            );
        }

        Match match = new Match(fixture);

        return matchRepository.save(match);
    }

    public Match getMatchById(Long id) {

        return matchRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Match not found with id: " + id
                        ));
    }

    @Transactional
    public Match recordResult(Long matchId, MatchResultRequest request) {

        Match match = getMatchById(matchId);

        int newHomeScore = request.getHomeScore();
        int newAwayScore = request.getAwayScore();

        Fixture fixture = match.getFixture();

        Team homeTeam = fixture.getHomeTeam();
        Team awayTeam = fixture.getAwayTeam();

        StandingsEntry homeStanding =
                getOrCreateStanding(homeTeam);

        StandingsEntry awayStanding =
                getOrCreateStanding(awayTeam);

        /*
         * If a previous result already exists,
         * remove its effect from the standings first.
         */
        if (match.isResultRecorded()) {

            int oldHomeScore = match.getHomeScore();
            int oldAwayScore = match.getAwayScore();

            // Remove old match statistics
            homeStanding.setPlayed(
                    Math.max(0, homeStanding.getPlayed() - 1)
            );

            awayStanding.setPlayed(
                    Math.max(0, awayStanding.getPlayed() - 1)
            );

            homeStanding.setGoalsScored(
                    homeStanding.getGoalsScored() - oldHomeScore
            );

            homeStanding.setGoalsConceded(
                    homeStanding.getGoalsConceded() - oldAwayScore
            );

            awayStanding.setGoalsScored(
                    awayStanding.getGoalsScored() - oldAwayScore
            );

            awayStanding.setGoalsConceded(
                    awayStanding.getGoalsConceded() - oldHomeScore
            );

            // Remove old Win / Draw / Loss and points
            removePreviousResult(
                    oldHomeScore,
                    oldAwayScore,
                    homeStanding,
                    awayStanding
            );
        }

        /*
         * Apply the new result
         */

        match.setHomeScore(newHomeScore);
        match.setAwayScore(newAwayScore);
        match.setResultRecorded(true);

        // Fixture remains completed
        fixture.setCompleted(true);
        fixtureRepository.save(fixture);

        // Add new common statistics
        homeStanding.setPlayed(
                homeStanding.getPlayed() + 1
        );

        awayStanding.setPlayed(
                awayStanding.getPlayed() + 1
        );

        homeStanding.setGoalsScored(
                homeStanding.getGoalsScored() + newHomeScore
        );

        homeStanding.setGoalsConceded(
                homeStanding.getGoalsConceded() + newAwayScore
        );

        awayStanding.setGoalsScored(
                awayStanding.getGoalsScored() + newAwayScore
        );

        awayStanding.setGoalsConceded(
                awayStanding.getGoalsConceded() + newHomeScore
        );

        // Apply new Win / Draw / Loss and points
        applyNewResult(
                newHomeScore,
                newAwayScore,
                homeStanding,
                awayStanding
        );

        standingsRepository.save(homeStanding);
        standingsRepository.save(awayStanding);

        return matchRepository.save(match);
    }

    /*
     * Removes the effect of the previous result
     * from both teams' standings.
     */
    private void removePreviousResult(
            int homeScore,
            int awayScore,
            StandingsEntry homeStanding,
            StandingsEntry awayStanding) {

        if (homeScore > awayScore) {

            homeStanding.setWins(
                    Math.max(0, homeStanding.getWins() - 1)
            );

            awayStanding.setLosses(
                    Math.max(0, awayStanding.getLosses() - 1)
            );

            homeStanding.setPoints(
                    Math.max(0,
                            homeStanding.getPoints() - winPoints)
            );

            awayStanding.setPoints(
                    Math.max(0,
                            awayStanding.getPoints() - lossPoints)
            );

        } else if (homeScore < awayScore) {

            awayStanding.setWins(
                    Math.max(0, awayStanding.getWins() - 1)
            );

            homeStanding.setLosses(
                    Math.max(0, homeStanding.getLosses() - 1)
            );

            awayStanding.setPoints(
                    Math.max(0,
                            awayStanding.getPoints() - winPoints)
            );

            homeStanding.setPoints(
                    Math.max(0,
                            homeStanding.getPoints() - lossPoints)
            );

        } else {

            homeStanding.setDraws(
                    Math.max(0, homeStanding.getDraws() - 1)
            );

            awayStanding.setDraws(
                    Math.max(0, awayStanding.getDraws() - 1)
            );

            homeStanding.setPoints(
                    Math.max(0,
                            homeStanding.getPoints() - drawPoints)
            );

            awayStanding.setPoints(
                    Math.max(0,
                            awayStanding.getPoints() - drawPoints)
            );
        }
    }

    /*
     * Applies the new result to both teams' standings.
     */
    private void applyNewResult(
            int homeScore,
            int awayScore,
            StandingsEntry homeStanding,
            StandingsEntry awayStanding) {

        if (homeScore > awayScore) {

            homeStanding.setWins(
                    homeStanding.getWins() + 1
            );

            awayStanding.setLosses(
                    awayStanding.getLosses() + 1
            );

            homeStanding.setPoints(
                    homeStanding.getPoints() + winPoints
            );

            awayStanding.setPoints(
                    awayStanding.getPoints() + lossPoints
            );

        } else if (homeScore < awayScore) {

            awayStanding.setWins(
                    awayStanding.getWins() + 1
            );

            homeStanding.setLosses(
                    homeStanding.getLosses() + 1
            );

            awayStanding.setPoints(
                    awayStanding.getPoints() + winPoints
            );

            homeStanding.setPoints(
                    homeStanding.getPoints() + lossPoints
            );

        } else {

            homeStanding.setDraws(
                    homeStanding.getDraws() + 1
            );

            awayStanding.setDraws(
                    awayStanding.getDraws() + 1
            );

            homeStanding.setPoints(
                    homeStanding.getPoints() + drawPoints
            );

            awayStanding.setPoints(
                    awayStanding.getPoints() + drawPoints
            );
        }
    }

    private StandingsEntry getOrCreateStanding(Team team) {

        return standingsRepository.findByTeamId(team.getId())
                .orElseGet(() ->
                        standingsRepository.save(
                                new StandingsEntry(team)
                        )
                );
    }
}