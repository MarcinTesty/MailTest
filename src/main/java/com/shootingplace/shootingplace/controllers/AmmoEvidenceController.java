package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.domain.entities.AmmoEvidenceEntity;
import com.shootingplace.shootingplace.domain.entities.AmmoInEvidenceEntity;
import com.shootingplace.shootingplace.domain.entities.AmmoUsedToEvidenceEntity;
import com.shootingplace.shootingplace.domain.entities.CaliberEntity;
import com.shootingplace.shootingplace.services.AmmoEvidenceService;
import com.shootingplace.shootingplace.services.AmmoInEvidenceService;
import com.shootingplace.shootingplace.services.AmmoUsedService;
import com.shootingplace.shootingplace.services.PersonalEvidenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ammoEvidence")
@CrossOrigin
public class AmmoEvidenceController {

    private final AmmoEvidenceService ammoEvidenceService;
    private final AmmoInEvidenceService ammoInEvidenceService;
    private final PersonalEvidenceService personalEvidenceService;
    private final AmmoUsedService ammoUsedService;

    public AmmoEvidenceController(AmmoEvidenceService ammoEvidenceService, AmmoInEvidenceService ammoInEvidenceService, PersonalEvidenceService personalEvidenceService, AmmoUsedService ammoUsedService) {
        this.ammoEvidenceService = ammoEvidenceService;
        this.ammoInEvidenceService = ammoInEvidenceService;
        this.personalEvidenceService = personalEvidenceService;
        this.ammoUsedService = ammoUsedService;
    }

//    @GetMapping("/")
//    public AmmoEvidenceEntity getAmmoEvidence() throws IOException, DocumentException {
//        return ammoEvidenceService.getAmmoEvidence();
//    }

    @GetMapping("/calibers")
    public ResponseEntity<List<CaliberEntity>> getCalibersList() {
        return ResponseEntity.ok(ammoEvidenceService.getCalibersList());
    }
//
//    @PutMapping("/addMember/{memberUUID}/{caliberUUID}")
//    public void addMemberToCaliber(@PathVariable UUID memberUUID, @PathVariable UUID caliberUUID, @RequestParam Integer quantity) throws IOException, DocumentException {
//        if (quantity > 0) {
//            ammoEvidenceService.addMemberToCaliber(memberUUID, caliberUUID, quantity);
//        }
//    }

//    @GetMapping("/map{memberUUID}/{caliberUUID}")
//    public Map<String, Integer> getMap(@PathVariable UUID memberUUID, @PathVariable UUID caliberUUID) {
//        return ammoEvidenceService.getMap(memberUUID, caliberUUID);
//    }
//
//    @GetMapping("/personal/{memberUUID}")
//    public void collectAmmoData(@PathVariable UUID memberUUID) {
//        personalEvidenceService.collectAmmoData(memberUUID);
//    }

    // New ammo used by Member

    @PostMapping("/ammo")
    public ResponseEntity<?> createAmmoUsed(@RequestParam UUID caliberUUID, @RequestParam UUID memberUUID, @RequestParam Integer counter) {
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

    @PatchMapping("/ammo")
    public ResponseEntity<?> closeEvidence(@RequestParam UUID evidenceUUID) {
        if (ammoEvidenceService.closeEvidence(evidenceUUID)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


    // helper
    @GetMapping("/ammoEvidence")
    public ResponseEntity<List<AmmoInEvidenceEntity>> getAllAmmoInEvidence() {
        return ResponseEntity.ok(ammoInEvidenceService.getAllAmmoInEvidence());
    }

    @GetMapping("/ammoUsed")
    public ResponseEntity<List<AmmoUsedToEvidenceEntity>> getAllsmth() {
        return ResponseEntity.ok(ammoUsedService.getAllsmth());
    }
}
