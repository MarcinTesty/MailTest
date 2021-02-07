package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.services.CompetitionMembersListService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/competitionMembersList")
@CrossOrigin
public class CompetitionMembersListController {

    private final CompetitionMembersListService competitionMembersListService;

    public CompetitionMembersListController(CompetitionMembersListService competitionMembersListService) {
        this.competitionMembersListService = competitionMembersListService;
    }

    @PutMapping("/addMember")
    public ResponseEntity<?> addScoreToCompetitionMembersList(@RequestParam String competitionUUID, @RequestParam int legitimationNumber, @RequestParam @Nullable int otherPerson) {
        if (competitionMembersListService.addScoreToCompetitionList(competitionUUID, legitimationNumber, otherPerson)) {
            return ResponseEntity.ok().build();
        } else
            return ResponseEntity.badRequest().build();
    }

    @PostMapping("/removeMember")
    public ResponseEntity<?> removeMemberFromList(@RequestParam String competitionUUID, @RequestParam int legitimationNumber, @RequestParam @Nullable int otherPerson) {
        if (competitionMembersListService.removeScoreFromList(competitionUUID, legitimationNumber, otherPerson)) {
            return ResponseEntity.ok().build();
        } else
            return ResponseEntity.badRequest().build();
    }

    @PatchMapping("/sort")
    public ResponseEntity<?> sortScoreByNameOrScore(@RequestParam String competitionUUID, @RequestParam boolean sort) {
        if (competitionMembersListService.sortScore(competitionUUID,sort)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


}
