package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.domain.entities.TournamentEntity;
import com.shootingplace.shootingplace.domain.models.Tournament;
import com.shootingplace.shootingplace.domain.models.TournamentDTO;
import com.shootingplace.shootingplace.services.TournamentService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/closedList")
    public ResponseEntity<List<TournamentDTO>> getListOfClosedTournaments() {
        return ResponseEntity.ok().body(tournamentService.getClosedTournaments());
    }

    @PostMapping("/")
    public ResponseEntity<String> addNewTournament(@RequestBody Tournament tournament) {
        return ResponseEntity.status(201).body(tournamentService.createNewTournament(tournament));
    }

    @PostMapping("/removeArbiter/{tournamentUUID}")
    public ResponseEntity<?> removeArbiterFromTournament(@PathVariable String tournamentUUID, @RequestParam int number, @RequestParam int id) {

        if (number > 0) {
            if (tournamentService.removeArbiterFromTournament(tournamentUUID, number)) {
                return ResponseEntity.ok().build();
            }
        }
        if (id > 0) {
            if (tournamentService.removeOtherArbiterFromTournament(tournamentUUID, id)) {
                return ResponseEntity.ok().build();
            }
        }

        return ResponseEntity.status(418).body("I'm a teapot");

    }

    @PatchMapping("/{tournamentUUID}")
    public ResponseEntity<?> closeTournament(@PathVariable String tournamentUUID) {
        if (tournamentService.closeTournament(tournamentUUID)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(418).body("I'm a teapot");
        }
    }

    @PutMapping("/{tournamentUUID}")
    public ResponseEntity<?> updateTournament(@PathVariable String tournamentUUID, @RequestBody Tournament tournament) {
        if (tournamentService.updateTournament(tournamentUUID, tournament)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/addMainArbiter/{tournamentUUID}")
    public ResponseEntity<?> addMainArbiter(@PathVariable String tournamentUUID, @RequestParam int number, @RequestParam int id) {

        if (number > 0) {
            if (tournamentService.addMainArbiter(tournamentUUID, number)) {
                return ResponseEntity.ok().build();
            }
        }
        if (id > 0) {
            if (tournamentService.addOtherMainArbiter(tournamentUUID, id)) {
                return ResponseEntity.ok().build();
            }
        }

        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/addRTSArbiter/{tournamentUUID}")
    public ResponseEntity<?> addRTSArbiter(@PathVariable String tournamentUUID, @RequestParam int number, @RequestParam int id) {

        if (number > 0) {
            if (tournamentService.addRTSArbiter(tournamentUUID, number)) {
                return ResponseEntity.ok().build();
            }
        }
        if (id > 0) {
            if (tournamentService.addOtherRTSArbiter(tournamentUUID, id)) {
                return ResponseEntity.ok().build();
            }
        }

        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/addOthersArbiters/{tournamentUUID}")
    public ResponseEntity<?> addOthersArbiters(@PathVariable String tournamentUUID, @RequestParam int number, @RequestParam int id) {

        if (number > 0) {
            if (tournamentService.addOthersArbiters(tournamentUUID, number)) {
                return ResponseEntity.ok().build();
            }
        }
        if (id > 0) {
            if (tournamentService.addPersonOthersArbiters(tournamentUUID, id)) {
                return ResponseEntity.ok().build();
            }
        }

        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/addCompetition/{tournamentUUID}")
    public ResponseEntity<?> addCompetitionListToTournament(@PathVariable String tournamentUUID, @RequestParam String competitionUUID) {
        if (tournamentService.addNewCompetitionListToTournament(tournamentUUID, competitionUUID)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @Transactional
    @DeleteMapping("/delete/{tournamentUUID}")
    public ResponseEntity<?> deleteTournament(@PathVariable String tournamentUUID) {
        if (tournamentService.deleteTournament(tournamentUUID)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
