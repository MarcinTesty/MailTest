package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.domain.entities.TournamentEntity;
import com.shootingplace.shootingplace.domain.models.Tournament;
import com.shootingplace.shootingplace.services.TournamentService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<TournamentEntity>> getListOfTournaments() {
        return ResponseEntity.ok(tournamentService.getListOfTournaments());
    }

    @PostMapping("/")
    public ResponseEntity<UUID> addNewTournament(@RequestBody Tournament tournament) {
        return ResponseEntity.status(201).body(tournamentService.createNewTournament(tournament));
    }

    @PutMapping("/{tournamentUUID}")
    public Boolean updateTournament(@PathVariable UUID tournamentUUID, @RequestBody Tournament tournament) {
        return tournamentService.updateTournament(tournamentUUID, tournament);
    }

    @PatchMapping("/{tournamentUUID}")
    public Boolean closeTournament(@PathVariable UUID tournamentUUID) {
        return tournamentService.closeTournament(tournamentUUID);
    }

    @PutMapping("/addMainArbiter/{tournamentUUID}")
    public void addMainArbiter(@PathVariable UUID tournamentUUID, @RequestParam UUID memberUUID) {
        tournamentService.addMainArbiter(tournamentUUID, memberUUID);
    }

    @PutMapping("/addRTSArbiter/{tournamentUUID}")
    public void addRTSArbiter(@PathVariable UUID tournamentUUID, @RequestParam UUID memberUUID) {
        tournamentService.addRTSArbiter(tournamentUUID, memberUUID);
    }
    @PutMapping("/addOthersArbiters/{tournamentUUID}")
    public void addOthersArbiters(@PathVariable UUID tournamentUUID, @RequestParam UUID memberUUID){
        tournamentService.addOthersArbiters(tournamentUUID, memberUUID);
    }

    @PutMapping("/addCompetition{tournamentUUID}")
    public void addNewCompetitionListToTournament(@PathVariable UUID tournamentUUID, @RequestParam UUID competitionUUID) {
        tournamentService.addNewCompetitionListToTournament(tournamentUUID, competitionUUID);
    }
}
