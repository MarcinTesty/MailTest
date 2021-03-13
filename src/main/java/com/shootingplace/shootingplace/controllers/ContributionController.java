package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.services.ChangeHistoryService;
import com.shootingplace.shootingplace.services.ContributionService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/contribution")
@CrossOrigin
public class ContributionController {

    private final ContributionService contributionService;
    private final ChangeHistoryService changeHistoryService;

    public ContributionController(ContributionService contributionService, ChangeHistoryService changeHistoryService) {
        this.contributionService = contributionService;
        this.changeHistoryService = changeHistoryService;
    }

    @Transactional
    @PostMapping("/history/{memberUUID}")
    public ResponseEntity<?> addHistoryContributionRecord(@PathVariable String memberUUID, @RequestParam String date, @RequestParam String pinCode) {
        if (changeHistoryService.comparePinCode(pinCode)) {
            if (contributionService.addContributionRecord(memberUUID, LocalDate.parse(date), pinCode)) {
                return ResponseEntity.ok("udało się");
            } else {
                return ResponseEntity.noContent().build();
            }
        }
        if (!changeHistoryService.comparePinCode(pinCode)) {
            return ResponseEntity.status(403).body("Brak uprawnień");

        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{memberUUID}")
    public ResponseEntity<?> addContribution(@PathVariable String memberUUID, @RequestParam String pinCode) {

        if (changeHistoryService.comparePinCode(pinCode)) {
            if (contributionService.addContribution(memberUUID, LocalDate.now(), pinCode)) {
                return ResponseEntity.ok("udało się");
            } else {
                return ResponseEntity.noContent().build();
            }
        }
        if (!changeHistoryService.comparePinCode(pinCode)) {
            return ResponseEntity.status(403).body("Brak uprawnień");

        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editContribution(@RequestParam String memberUUID, @RequestParam String contributionUUID, @RequestParam String paymentDay, @RequestParam String validThru, @RequestParam String pinCode) {
        if (changeHistoryService.comparePinCode(pinCode)) {
            LocalDate parsedPaymentDay = null;
            if (paymentDay != null && !paymentDay.isEmpty() && !paymentDay.equals("null")) {
                parsedPaymentDay = LocalDate.parse(paymentDay);
            }
            LocalDate parsedValidThru = null;
            if(validThru!=null && !validThru.isEmpty() && !validThru.equals("null")){
                parsedValidThru = LocalDate.parse(validThru);
            }

            if (contributionService.updateContribution(memberUUID, contributionUUID, parsedPaymentDay,parsedValidThru,pinCode )) {
                return ResponseEntity.ok("udało się");
            } else {
                return ResponseEntity.noContent().build();
            }
        }
        if (!changeHistoryService.comparePinCode(pinCode)) {
            return ResponseEntity.status(403).body("Brak uprawnień");

        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/remove/{memberUUID}")
    public ResponseEntity<?> removeContribution(@PathVariable String memberUUID, @RequestParam String contributionUUID, @RequestParam String pinCode) {
        if (changeHistoryService.comparePinCode(pinCode)) {
            if (contributionService.removeContribution(memberUUID, contributionUUID, pinCode)) {
                return ResponseEntity.ok("udało się");
            } else {
                return ResponseEntity.noContent().build();
            }
        }
        if (!changeHistoryService.comparePinCode(pinCode)) {
            return ResponseEntity.status(403).body("Brak uprawnień");

        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
