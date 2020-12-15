package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.services.CompetitionMembersListService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> addMemberToCompetitionMembersList(@RequestParam UUID competitionUUID, @RequestParam UUID memberUUID) {
        if (competitionMembersListService.addMemberToCompetitionList(competitionUUID, memberUUID)) {
            return ResponseEntity.ok().build();
        } else
            return ResponseEntity.badRequest().build();
    }

    @PostMapping("/removeMember")
    public ResponseEntity<?> removeMemberFromList(@RequestParam UUID competitionUUID, @RequestParam UUID memberUUID) {
        if (competitionMembersListService.removeMemberFromList(competitionUUID, memberUUID)) {
            return ResponseEntity.ok().build();
        } else
            return ResponseEntity.badRequest().build();
    }

}
