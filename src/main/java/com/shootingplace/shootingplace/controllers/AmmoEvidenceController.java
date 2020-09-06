package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.domain.entities.AmmoEvidenceEntity;
import com.shootingplace.shootingplace.domain.entities.CaliberEntity;
import com.shootingplace.shootingplace.services.AmmoEvidenceService;
import com.shootingplace.shootingplace.services.PersonalEvidenceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/ammoEvidence")
@CrossOrigin
public class AmmoEvidenceController {

    private final AmmoEvidenceService ammoEvidenceService;
    private final PersonalEvidenceService personalEvidenceService;

    public AmmoEvidenceController(AmmoEvidenceService ammoEvidenceService, PersonalEvidenceService personalEvidenceService) {
        this.ammoEvidenceService = ammoEvidenceService;
        this.personalEvidenceService = personalEvidenceService;
    }

    @GetMapping("/")
    public AmmoEvidenceEntity getAmmoEvidence(){
        return ammoEvidenceService.getAmmoEvidence();
    }
    @GetMapping("/calibers")
    public List<CaliberEntity> getCalibersList(){
        return ammoEvidenceService.getCalibersList();
    }

    @PutMapping("/addMember{memberUUID}/{caliberUUID}")
    public void addMemberToCaliber(@PathVariable UUID memberUUID,@PathVariable UUID caliberUUID,@RequestParam Integer quantity){
        ammoEvidenceService.addMemberToCaliber(memberUUID,caliberUUID,quantity);
    }

    @GetMapping("/map{memberUUID}/{caliberUUID}")
    public Map<String,Integer> getMap(@PathVariable UUID memberUUID,@PathVariable UUID caliberUUID){
        return ammoEvidenceService.getMap(memberUUID,caliberUUID);
    }

    @GetMapping("/personal{memberUUID}")
    public void collectAmmoData(@PathVariable UUID memberUUID){
        personalEvidenceService.collectAmmoData(memberUUID);
    }

}
