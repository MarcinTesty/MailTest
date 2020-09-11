package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.domain.entities.CompetitionEntity;
import com.shootingplace.shootingplace.services.CompetitionService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public List<CompetitionEntity> getAllCompetitions() {
        return competitionService.getAllCompetitions();
    }

}
