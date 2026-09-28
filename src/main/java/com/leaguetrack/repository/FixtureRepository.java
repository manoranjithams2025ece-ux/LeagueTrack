package com.leaguetrack.repository;

import com.leaguetrack.entity.Fixture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FixtureRepository extends JpaRepository<Fixture, Long> {
}