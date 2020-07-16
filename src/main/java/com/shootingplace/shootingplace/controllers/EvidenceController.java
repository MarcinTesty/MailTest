package com.shootingplace.shootingplace.controllers;

import com.shootingplace.shootingplace.domain.entities.ElectronicEvidenceEntity;
import com.shootingplace.shootingplace.services.ElectronicEvidenceService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/evidence")
@CrossOrigin
public class EvidenceController {

    private final ElectronicEvidenceService evidenceService;

    public EvidenceController(ElectronicEvidenceService evidenceService) {
        this.evidenceService = evidenceService;
    }

    @GetMapping("/{id}")
    public ElectronicEvidenceEntity getMembersInEvidence(Integer id){
        return evidenceService.getMembersInEvidence(id);
    }
}
