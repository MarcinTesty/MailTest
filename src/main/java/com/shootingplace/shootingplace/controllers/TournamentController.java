package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.domain.entities.TournamentEntity;
import com.shootingplace.shootingplace.domain.models.Tournament;
import com.shootingplace.shootingplace.services.TournamentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tournament")
@CrossOrigin
public class TournamentController {

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @GetMapping("/list")
    public List<TournamentEntity> getListOfTournaments() {
        return tournamentService.getListOfTournaments();
    }

    @PostMapping("/")
    public UUID addNewTournament(@RequestBody Tournament tournament) {
        return tournamentService.createNewTournament(tournament);
    }

    @PutMapping("/{tournamentUUID}")
    public Boolean updateTournament(@PathVariable UUID tournamentUUID, @RequestBody Tournament tournament) {
        return tournamentService.updateTournament(tournamentUUID, tournament);
    }

    @PutMapping("/addMember{tournamentUUID}")
    public Boolean addMemberToCompetitionsMembersList(@PathVariable UUID tournamentUUID, @RequestParam UUID memberUUID) {
        return tournamentService.addMemberToCompetitionMembersList(tournamentUUID, memberUUID);
    }

    @PatchMapping("/{tournamentUUID}")
    public Boolean closeTournament(@PathVariable UUID tournamentUUID) {
        return tournamentService.closeTournament(tournamentUUID);
    }

    @PutMapping("/addMainArbiter{tournamentUUID}")
    public void addMainArbiter(@PathVariable UUID tournamentUUID, @RequestParam UUID memberUUID) {
        tournamentService.addMainArbiter(tournamentUUID, memberUUID);
    }

    @PutMapping("/addRTSArbiter{tournamentUUID}")
    public void addRTSArbiter(@PathVariable UUID tournamentUUID, @RequestParam String memberLegitimation) {
        tournamentService.addRTSArbiter(tournamentUUID, memberLegitimation);
    }

    @PutMapping("/addCompetition{tournamentUUID}")
    public void addNewCompetitionListToTournament(@PathVariable UUID tournamentUUID, @RequestParam UUID competitionUUID) {
        tournamentService.addNewCompetitionListToTournament(tournamentUUID, competitionUUID);
    }
}
