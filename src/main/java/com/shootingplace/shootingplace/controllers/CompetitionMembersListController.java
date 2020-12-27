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
    public ResponseEntity<?> addMemberToCompetitionMembersList(@RequestParam UUID competitionUUID, @RequestParam int legitimationNumber) {
        if (competitionMembersListService.addMemberToCompetitionList(competitionUUID, legitimationNumber)) {
            return ResponseEntity.ok().build();
        } else
            return ResponseEntity.badRequest().build();
    }

    @PostMapping("/removeMember")
    public ResponseEntity<?> removeMemberFromList(@RequestParam UUID competitionUUID, @RequestParam int legitimationNumber) {
        if (competitionMembersListService.removeMemberFromList(competitionUUID, legitimationNumber)) {
            return ResponseEntity.ok().build();
        } else
            return ResponseEntity.badRequest().build();
    }

}
