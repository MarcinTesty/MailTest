package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.domain.entities.AmmoEvidenceEntity;
import com.shootingplace.shootingplace.domain.entities.CaliberEntity;
import com.shootingplace.shootingplace.domain.models.AmmoDTO;
import com.shootingplace.shootingplace.services.AmmoEvidenceService;
import com.shootingplace.shootingplace.services.AmmoUsedService;
import com.shootingplace.shootingplace.services.CaliberService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ammoEvidence")
@CrossOrigin
public class AmmoEvidenceController {

    private final AmmoEvidenceService ammoEvidenceService;
    private final AmmoUsedService ammoUsedService;
    private final CaliberService caliberService;

    public AmmoEvidenceController(AmmoEvidenceService ammoEvidenceService, AmmoUsedService ammoUsedService, CaliberService caliberService) {
        this.ammoEvidenceService = ammoEvidenceService;
        this.ammoUsedService = ammoUsedService;
        this.caliberService = caliberService;
    }


    @GetMapping("/calibers")
    public ResponseEntity<List<CaliberEntity>> getCalibersList() {
        return ResponseEntity.ok(ammoEvidenceService.getCalibersList());
    }

    @PostMapping("/calibers")
    public ResponseEntity<?> createNewCaliber(@RequestParam String caliber) {
        if (caliber.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (caliberService.createNewCaliber(caliber)) {
            return ResponseEntity.status(201).build();
        } else
            return ResponseEntity.badRequest().build();
    }

    // New ammo used by Member
    @Transactional
    @PostMapping("/ammo")
    public ResponseEntity<?> createAmmoUsed(@RequestParam String caliberUUID, @RequestParam Integer legitimationNumber, @RequestParam int otherID, @RequestParam Integer counter) {

        if (ammoUsedService.addAmmoUsedEntity(caliberUUID, legitimationNumber, otherID, counter)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }

    }

    // New ammo in evidence

    @GetMapping("/evidence")
    public ResponseEntity<List<AmmoEvidenceEntity>> getAllEvidences(@RequestParam boolean state) {
        return ResponseEntity.ok(ammoEvidenceService.getAllEvidences(state));
    }
    @GetMapping("/oneEvidence")
    public ResponseEntity<AmmoEvidenceEntity> getEvidence() {
        return ResponseEntity.ok(ammoEvidenceService.getEvidence());
    }

    @GetMapping("/closedEvidences")
    public ResponseEntity<List<AmmoDTO>> getClosedEvidence() {
        return ResponseEntity.ok().body(ammoEvidenceService.getClosedEvidences());
    }

    @PatchMapping("/ammo")
    public ResponseEntity<?> closeEvidence(@RequestParam String evidenceUUID) {
        if (ammoEvidenceService.closeEvidence(evidenceUUID)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}
