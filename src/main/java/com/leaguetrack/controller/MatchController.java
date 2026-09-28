package com.leaguetrack.controller;

import com.leaguetrack.dto.MatchResultRequest;
import com.leaguetrack.entity.Match;
import com.leaguetrack.service.MatchService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping("/fixture/{fixtureId}")
    public Match createMatch(@PathVariable Long fixtureId) {
        return matchService.createMatch(fixtureId);
    }

    @PutMapping("/{matchId}/result")
    public Match recordResult(
            @PathVariable Long matchId,
            @Valid @RequestBody MatchResultRequest request) {

        return matchService.recordResult(matchId, request);
    }

    @GetMapping("/{id}")
    public Match getMatchById(@PathVariable Long id) {
        return matchService.getMatchById(id);
    }
}