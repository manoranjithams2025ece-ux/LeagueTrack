package com.leaguetrack.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "fixture_id", nullable = false, unique = true)
    private Fixture fixture;

    private Integer homeScore;

    private Integer awayScore;

    private boolean resultRecorded = false;

    public Match() {
    }

    public Match(Fixture fixture) {
        this.fixture = fixture;
        this.resultRecorded = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Fixture getFixture() {
        return fixture;
    }

    public void setFixture(Fixture fixture) {
        this.fixture = fixture;
    }

    public Integer getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(Integer homeScore) {
        this.homeScore = homeScore;
    }

    public Integer getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(Integer awayScore) {
        this.awayScore = awayScore;
    }

    public boolean isResultRecorded() {
        return resultRecorded;
    }

    public void setResultRecorded(boolean resultRecorded) {
        this.resultRecorded = resultRecorded;
    }
}