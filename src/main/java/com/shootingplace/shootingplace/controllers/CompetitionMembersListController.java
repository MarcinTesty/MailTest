package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.services.CompetitionMembersListService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/competitionMembersList")
@CrossOrigin
public class CompetitionMembersListController {

    private final CompetitionMembersListService competitionMembersListService;

    public CompetitionMembersListController(CompetitionMembersListService competitionMembersListService) {
        this.competitionMembersListService = competitionMembersListService;
    }

    @PutMapping("/addMember")
    public void addMemberToCompetitionMembersList(@RequestParam UUID competitionUUID, @RequestParam UUID memberUUID) {
        competitionMembersListService.addMemberToCompetitionList(competitionUUID, memberUUID);
    }

}
