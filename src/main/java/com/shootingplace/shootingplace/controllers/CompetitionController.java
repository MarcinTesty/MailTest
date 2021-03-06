package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.domain.entities.CompetitionEntity;
import com.shootingplace.shootingplace.services.CompetitionService;
import com.shootingplace.shootingplace.services.ScoreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/competition")
@CrossOrigin
public class CompetitionController {
    private final CompetitionService competitionService;
    private final ScoreService scoreService;

    public CompetitionController(CompetitionService competitionService, ScoreService scoreService) {
        this.competitionService = competitionService;
        this.scoreService = scoreService;
    }

    @GetMapping("/")
    public ResponseEntity<List<CompetitionEntity>> getAllCompetitions() {
        return ResponseEntity.ok(competitionService.getAllCompetitions());
    }

    @PostMapping("")
    public ResponseEntity<?> createCompetition(@RequestParam String name, @RequestParam String discipline) {
        if (name.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        String trim = discipline.trim();
        if (competitionService.createNewCompetition(name, trim)) {
            return ResponseEntity.status(201).build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("")
    public ResponseEntity<?> setScore(@RequestParam String scoreUUID, @RequestParam float score, @RequestParam float innerTen, @RequestParam float outerTen) {

        if (scoreService.setScore(scoreUUID, score, innerTen, outerTen)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/ammo")
    public ResponseEntity<?> toggleAmmunitionInScore(@RequestParam String scoreUUID) {

        if (scoreService.toggleAmmunitionInScore(scoreUUID)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/gun")
    public ResponseEntity<?> toggleGunInScore(@RequestParam String scoreUUID) {

        if (scoreService.toggleGunInScore(scoreUUID)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}
