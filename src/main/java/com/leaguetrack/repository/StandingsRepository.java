package com.leaguetrack.repository;

import com.leaguetrack.entity.StandingsEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StandingsRepository extends JpaRepository<StandingsEntry, Long> {

    Optional<StandingsEntry> findByTeamId(Long teamId);

    void deleteByTeamId(Long teamId);
}