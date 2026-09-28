package com.leaguetrack.controller;

import com.leaguetrack.service.TournamentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/tournament")
public class TournamentController {

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @GetMapping("/summary")
    public Map<String, Object> getTournamentSummary() {
        return tournamentService.getTournamentSummary();
    }
}