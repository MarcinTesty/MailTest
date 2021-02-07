package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.services.ContributionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/contribution")
@CrossOrigin
public class ContributionController {

    private final ContributionService contributionService;

    public ContributionController(ContributionService contributionService) {
        this.contributionService = contributionService;
    }

    @PostMapping("/history/{memberUUID}")
    public ResponseEntity<?> addHistoryContributionRecord(@PathVariable String memberUUID, @RequestParam String date) {
        if (contributionService.addContributionRecord(memberUUID, LocalDate.parse(date))) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PatchMapping("/{memberUUID}")
    public ResponseEntity<?> addContribution(@PathVariable String memberUUID) {
        if (contributionService.addContribution(memberUUID, LocalDate.now())) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PatchMapping("/remove/{memberUUID}")
    public ResponseEntity<?> removeContribution(@PathVariable String memberUUID, @RequestParam String contributionUUID) {
        if (contributionService.removeContribution(memberUUID, contributionUUID)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
