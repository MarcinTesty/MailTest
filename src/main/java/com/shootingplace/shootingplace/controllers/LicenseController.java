package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.domain.models.License;
import com.shootingplace.shootingplace.services.HistoryService;
import com.shootingplace.shootingplace.services.LicenseService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @PostMapping("/{memberUUID}")
    public boolean addLicense(@PathVariable UUID memberUUID, @RequestBody License license) {
        return licenseService.addLicenseToMember(memberUUID, license);
    }

    @PutMapping("/{memberUUID}")
    public boolean updateLicense(@PathVariable UUID memberUUID, @RequestBody License license) {
        return licenseService.updateLicense(memberUUID, license);
    }

    @PatchMapping("/{memberUUID}")
    public boolean renewLicenseValidDate(@PathVariable UUID memberUUID, @RequestBody License license) {
        return licenseService.renewLicenseValid(memberUUID,license);
    }
    @PutMapping("/history{memberUUID}")
    public Boolean addLicensePaymentHistory(@PathVariable UUID memberUUID){
        return historyService.addLicenseHistoryPayment(memberUUID);
    }
    @PutMapping("/historyrec{memberUUID}")
    public Boolean addLicensePaymentHistory(@PathVariable UUID memberUUID, @RequestParam LocalDate date){
        return historyService.addLicenseHistoryPaymentRecord(memberUUID,date);
    }

}
