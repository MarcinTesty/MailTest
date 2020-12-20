package com.shootingplace.shootingplace.controllers;

import com.itextpdf.text.DocumentException;
import com.shootingplace.shootingplace.domain.entities.AmmoEvidenceEntity;
import com.shootingplace.shootingplace.domain.entities.AmmoUsedEntity;
import com.shootingplace.shootingplace.domain.entities.CaliberEntity;
import com.shootingplace.shootingplace.services.AmmoEvidenceService;
import com.shootingplace.shootingplace.services.AmmoUsedService;
import com.shootingplace.shootingplace.services.PersonalEvidenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/ammoEvidence")
@CrossOrigin
public class AmmoEvidenceController {

    private final AmmoEvidenceService ammoEvidenceService;
    private final PersonalEvidenceService personalEvidenceService;
    private final AmmoUsedService ammoUsedService;

    public AmmoEvidenceController(AmmoEvidenceService ammoEvidenceService, PersonalEvidenceService personalEvidenceService, AmmoUsedService ammoUsedService) {
        this.ammoEvidenceService = ammoEvidenceService;
        this.personalEvidenceService = personalEvidenceService;
        this.ammoUsedService = ammoUsedService;
    }

    @GetMapping("/")
    public AmmoEvidenceEntity getAmmoEvidence() throws IOException, DocumentException {
        return ammoEvidenceService.getAmmoEvidence();
    }

    @GetMapping("/calibers")
    public ResponseEntity<List<CaliberEntity>> getCalibersList() {
        return ResponseEntity.ok(ammoEvidenceService.getCalibersList());
    }

    @PutMapping("/addMember/{memberUUID}/{caliberUUID}")
    public void addMemberToCaliber(@PathVariable UUID memberUUID, @PathVariable UUID caliberUUID, @RequestParam Integer quantity) throws IOException, DocumentException {
        if (quantity > 0) {
            ammoEvidenceService.addMemberToCaliber(memberUUID, caliberUUID, quantity);
        }
    }

    @GetMapping("/map{memberUUID}/{caliberUUID}")
    public Map<String, Integer> getMap(@PathVariable UUID memberUUID, @PathVariable UUID caliberUUID) {
        return ammoEvidenceService.getMap(memberUUID, caliberUUID);
    }

    @GetMapping("/personal/{memberUUID}")
    public void collectAmmoData(@PathVariable UUID memberUUID) {
        personalEvidenceService.collectAmmoData(memberUUID);
    }

    // New ammo used by Member


    @GetMapping("/ammo")
    public ResponseEntity<List<AmmoUsedEntity>> getAllAmmoUsed() {
        return ResponseEntity.ok(ammoUsedService.getAllAmmoUsed());
    }

    @PostMapping("/ammo")
    public ResponseEntity<?> createAmmoUsed(@RequestParam UUID caliberUUID, @RequestParam UUID memberUUID, @RequestParam Integer counter) {
        if (ammoUsedService.addAmmoUsedEntity(caliberUUID, memberUUID, counter)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }

    }

}
