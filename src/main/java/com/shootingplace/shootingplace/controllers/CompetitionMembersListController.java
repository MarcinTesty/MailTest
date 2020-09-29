package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.repositories.CompetitionRepository;
import com.shootingplace.shootingplace.services.CompetitionMembersListService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/competitionMembersList")
@CrossOrigin
public class CompetitionMembersListController {

    private final CompetitionMembersListService competitionMembersListService;
    private final CompetitionRepository competitionRepository;

    public CompetitionMembersListController(CompetitionMembersListService competitionMembersListService, CompetitionRepository competitionRepository) {
        this.competitionMembersListService = competitionMembersListService;
        this.competitionRepository = competitionRepository;
    }

    @PutMapping("/addMember")
    public void addMemberToCompetitionMembersList(@RequestParam UUID competitionUUID, @RequestParam UUID memberUUID) {
        competitionMembersListService.addMemberToCompetitionList(competitionUUID, memberUUID);
    }

}
