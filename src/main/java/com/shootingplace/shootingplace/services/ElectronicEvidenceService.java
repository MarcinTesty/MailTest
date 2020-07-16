package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.ElectronicEvidenceEntity;
import com.shootingplace.shootingplace.repositories.ElectronicEvidenceRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class ElectronicEvidenceService {

    private final ElectronicEvidenceRepository evidenceRepository;

    public ElectronicEvidenceService(ElectronicEvidenceRepository evidenceRepository) {
        this.evidenceRepository = evidenceRepository;
    }

    public ElectronicEvidenceEntity getMembersInEvidence(Integer id) {
        geyOrCreateEvidenceEntity();

        return evidenceRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    private void geyOrCreateEvidenceEntity() {

    }
}
