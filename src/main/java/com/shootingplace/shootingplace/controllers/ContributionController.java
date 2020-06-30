package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.domain.models.Contribution;
import com.shootingplace.shootingplace.services.ContributionService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/contribution")
public class ContributionController {

    private final ContributionService contributionService;

    public ContributionController(ContributionService contributionService) {
        this.contributionService = contributionService;
    }

    @PostMapping("/{memberUUID}")
    public boolean addContribution(@PathVariable UUID memberUUID, @RequestBody Contribution contribution) {
        return contributionService.addContribution(memberUUID, contribution);
    }

    @PutMapping("/{memberUUID}")
    public boolean updateContribution(@PathVariable UUID memberUUID, @RequestBody Contribution contribution) {
        return contributionService.updateContribution(memberUUID, contribution);
    }

    @PatchMapping("/{memberUUID}")
    public boolean prolongContribution(@PathVariable UUID memberUUID, @RequestParam("a") Integer contributionAmount) {
        return contributionService.prolongContribution(memberUUID, contributionAmount);
    }
}
