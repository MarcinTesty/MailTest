package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.domain.models.License;
import com.shootingplace.shootingplace.services.HistoryService;
import com.shootingplace.shootingplace.services.LicenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/license")
@CrossOrigin
public class LicenseController {

    private final LicenseService licenseService;
    private final HistoryService historyService;

    public LicenseController(LicenseService licenseService, HistoryService historyService) {
        this.licenseService = licenseService;
        this.historyService = historyService;
    }

    @GetMapping("/")
    public Map<UUID, License> getLicense() {
        return licenseService.getLicense();
    }

    @GetMapping("/members")
    public Map<String, License> getMembersNamesAndLicense() {
        return licenseService.getMembersNamesAndLicense();
    }

    @PutMapping("/{memberUUID}")
    public ResponseEntity<?> updateLicense(@PathVariable UUID memberUUID, @RequestBody License license) {
        if (licenseService.updateLicense(memberUUID, license)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{memberUUID}")
    public ResponseEntity<?> renewLicenseValidDate(@PathVariable UUID memberUUID, @RequestBody License license) {
        if (licenseService.renewLicenseValid(memberUUID, license)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/history/{memberUUID}")
    public ResponseEntity<?> addLicensePaymentHistory(@PathVariable UUID memberUUID) {
        if (historyService.addLicenseHistoryPayment(memberUUID)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}
