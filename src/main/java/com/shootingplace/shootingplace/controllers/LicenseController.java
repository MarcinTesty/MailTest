package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.domain.models.License;
import com.shootingplace.shootingplace.services.LicenseService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/license")
public class LicenseController {

    private final LicenseService licenseService;

    public LicenseController(LicenseService licenseService) {
        this.licenseService = licenseService;
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
    public boolean renewLicenseValidDate(@PathVariable UUID memberUUID) {
        return licenseService.setOrRenewLicenseValid(memberUUID);
    }

}
