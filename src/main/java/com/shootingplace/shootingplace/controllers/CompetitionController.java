package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.domain.entities.CompetitionEntity;
import com.shootingplace.shootingplace.domain.models.Competition;
import com.shootingplace.shootingplace.services.CompetitionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/competition")
@CrossOrigin
public class CompetitionController {
    private final CompetitionService competitionService;

    public CompetitionController(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }

    @GetMapping("/")
    public ResponseEntity<List<CompetitionEntity>> getAllCompetitions() {
        return ResponseEntity.ok(competitionService.getAllCompetitions());
    }

    @PostMapping("/")
    public ResponseEntity<?> createCompetition(@RequestBody Competition competition) {
        if (competitionService.createNewCompetition(competition)) {
           return ResponseEntity.status(201).build();
        }
        else {
           return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

}
