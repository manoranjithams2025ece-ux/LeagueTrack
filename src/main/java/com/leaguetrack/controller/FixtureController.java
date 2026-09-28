package com.leaguetrack.controller;

import com.leaguetrack.entity.Fixture;
import com.leaguetrack.service.FixtureService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fixtures")
public class FixtureController {

    private final FixtureService fixtureService;

    public FixtureController(FixtureService fixtureService) {
        this.fixtureService = fixtureService;
    }

    @PostMapping("/generate")
    public List<Fixture> generateFixtures() {
        return fixtureService.generateFixtures();
    }

    @GetMapping
    public List<Fixture> getAllFixtures() {
        return fixtureService.getAllFixtures();
    }

    @GetMapping("/{id}")
    public Fixture getFixtureById(@PathVariable Long id) {
        return fixtureService.getFixtureById(id);
    }
}