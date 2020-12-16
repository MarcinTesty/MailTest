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

    @PostMapping("/removeArbiter/{tournamentUUID}")
    public ResponseEntity<?> removeArbiterFromTournament(@PathVariable UUID tournamentUUID, @RequestParam UUID memberUUID) {
        if (tournamentService.removeArbiterFromTournament(tournamentUUID, memberUUID)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(418).body("I'm a teapot");

    }

    @PatchMapping("/{tournamentUUID}")
    public ResponseEntity<?> closeTournament(@PathVariable UUID tournamentUUID) {
        if (tournamentService.closeTournament(tournamentUUID)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(418).body("I'm a teapot");
        }
    }

    @PutMapping("/{tournamentUUID}")
    public ResponseEntity<?> updateTournament(@PathVariable UUID tournamentUUID, @RequestBody Tournament tournament) {
        if (tournamentService.updateTournament(tournamentUUID, tournament)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/addMainArbiter/{tournamentUUID}")
    public ResponseEntity<?> addMainArbiter(@PathVariable UUID tournamentUUID, @RequestParam UUID memberUUID) {
        if (tournamentService.addMainArbiter(tournamentUUID, memberUUID)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/addRTSArbiter/{tournamentUUID}")
    public ResponseEntity<?> addRTSArbiter(@PathVariable UUID tournamentUUID, @RequestParam UUID memberUUID) {
        if (tournamentService.addRTSArbiter(tournamentUUID, memberUUID)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/addOthersArbiters/{tournamentUUID}")
    public ResponseEntity<?> addOthersArbiters(@PathVariable UUID tournamentUUID, @RequestParam UUID memberUUID) {
        if (tournamentService.addOthersArbiters(tournamentUUID, memberUUID)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/addCompetition/{tournamentUUID}")
    public ResponseEntity<?> addCompetitionListToTournament(@PathVariable UUID tournamentUUID, @RequestParam UUID competitionUUID) {
        if (tournamentService.addNewCompetitionListToTournament(tournamentUUID, competitionUUID)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
