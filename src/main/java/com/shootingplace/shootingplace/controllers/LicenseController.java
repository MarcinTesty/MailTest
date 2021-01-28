package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.domain.models.License;
import com.shootingplace.shootingplace.services.HistoryService;
import com.shootingplace.shootingplace.services.LicenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/license")
public class LicenseController {

    private final LicenseService licenseService;
    private final HistoryService historyService;

    public LicenseController(LicenseService licenseService, HistoryService historyService) {
        this.licenseService = licenseService;
        this.historyService = historyService;
    }

    @GetMapping("/members")
    public Map<String, License> getMembersNamesAndLicense() {
        return licenseService.getMembersNamesAndLicense();
    }

    @PutMapping("/{memberUUID}")
    public ResponseEntity<?> updateLicense(@PathVariable String memberUUID, @RequestBody License license) {
        if (licenseService.updateLicense(memberUUID, license)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{memberUUID}")
    public ResponseEntity<?> renewLicenseValidDate(@PathVariable String memberUUID, @RequestBody License license) {
        if (licenseService.renewLicenseValid(memberUUID, license)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/history/{memberUUID}")
    public ResponseEntity<?> addLicensePaymentHistory(@PathVariable String memberUUID) {
        if (historyService.addLicenseHistoryPayment(memberUUID)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}
