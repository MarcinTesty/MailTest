package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.services.ContributionService;
import com.shootingplace.shootingplace.services.HistoryService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contribution")
@CrossOrigin
public class ContributionController {

    private final ContributionService contributionService;
    private final HistoryService historyService;

    public ContributionController(ContributionService contributionService, HistoryService historyService) {
        this.contributionService = contributionService;
        this.historyService = historyService;
    }

//    @PutMapping("/{memberUUID}")
//    public void updateContribution(@PathVariable UUID memberUUID, @RequestBody Contribution contribution) {
//        contributionService.updateContribution(memberUUID, contribution);
//    }

//    @PutMapping("/history{memberUUID}")
//    public boolean addHistoryContributionRecord(@PathVariable UUID memberUUID, @RequestParam String date) {
//        return historyService.addContributionRecord(memberUUID, date);
//    }

//    @PatchMapping("/{memberUUID}")
//    public ResponseEntity<?> prolongContribution(@PathVariable UUID memberUUID) {
//        if (contributionService.prolongContribution(memberUUID)){
//            return ResponseEntity.ok().build();
//        }
//        else {return ResponseEntity.noContent().build();}
//    }
}
