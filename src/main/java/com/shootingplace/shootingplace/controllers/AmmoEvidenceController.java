package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.domain.entities.AmmoEvidenceEntity;
import com.shootingplace.shootingplace.domain.entities.CaliberEntity;
import com.shootingplace.shootingplace.domain.models.AmmoDTO;
import com.shootingplace.shootingplace.services.AmmoEvidenceService;
import com.shootingplace.shootingplace.services.AmmoUsedService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ammoEvidence")
@CrossOrigin
public class AmmoEvidenceController {

    private final AmmoEvidenceService ammoEvidenceService;
    private final AmmoUsedService ammoUsedService;

    public AmmoEvidenceController(AmmoEvidenceService ammoEvidenceService, AmmoUsedService ammoUsedService) {
        this.ammoEvidenceService = ammoEvidenceService;
        this.ammoUsedService = ammoUsedService;
    }


    @GetMapping("/calibers")
    public ResponseEntity<List<CaliberEntity>> getCalibersList() {
        return ResponseEntity.ok(ammoEvidenceService.getCalibersList());
    }

    // New ammo used by Member

    @PostMapping("/ammo")
    public ResponseEntity<?> createAmmoUsed(@RequestParam UUID caliberUUID, @RequestParam UUID memberUUID, @RequestParam Integer counter) {
        System.out.println(memberUUID);
        if (ammoUsedService.addAmmoUsedEntity(caliberUUID, memberUUID, counter)) {
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
    @GetMapping("/closedEvidences")
    public ResponseEntity<List<AmmoDTO>> getClosedEvidence(){
        return ResponseEntity.ok().body(ammoEvidenceService.getClosedEvidences());
    }

    @PatchMapping("/ammo")
    public ResponseEntity<?> closeEvidence(@RequestParam UUID evidenceUUID) {
        if (ammoEvidenceService.closeEvidence(evidenceUUID)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


//    // helper
//    @GetMapping("/ammoEvidence")
//    public ResponseEntity<List<AmmoInEvidenceEntity>> getAllAmmoInEvidence() {
//        return ResponseEntity.ok(ammoInEvidenceService.getAllAmmoInEvidence());
//    }
//
//    @GetMapping("/ammoUsed")
//    public ResponseEntity<List<AmmoUsedToEvidenceEntity>> getAllsmth() {
//        return ResponseEntity.ok(ammoUsedService.getAllsmth());
//    }
}
