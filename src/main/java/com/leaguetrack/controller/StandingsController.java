package com.leaguetrack.controller;

import com.leaguetrack.entity.StandingsEntry;
import com.leaguetrack.service.StandingsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/standings")
public class StandingsController {

    private final StandingsService standingsService;

    public StandingsController(StandingsService standingsService) {
        this.standingsService = standingsService;
    }

    @GetMapping
    public List<StandingsEntry> getStandings() {
        return standingsService.getStandings();
    }
}