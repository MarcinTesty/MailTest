package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.AmmoEvidenceEntity;
import com.shootingplace.shootingplace.domain.entities.CaliberEntity;
import com.shootingplace.shootingplace.repositories.AmmoEvidenceRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AmmoEvidenceService {

    private final CaliberService caliberService;
    private final AmmoEvidenceRepository ammoEvidenceRepository;
    private final Logger LOG = LogManager.getLogger(getClass());


    public AmmoEvidenceService(CaliberService caliberService, AmmoEvidenceRepository ammoEvidenceRepository) {
        this.caliberService = caliberService;
        this.ammoEvidenceRepository = ammoEvidenceRepository;
    }


    private void addAmmoEvidence() {
        AmmoEvidenceEntity ammoEvidenceEntity = AmmoEvidenceEntity.builder()
                .date(LocalDate.now())
                .label("Ewidencja zużycia amunicji")
                .caliberList(null)
                .file(null)
                .build();

        ammoEvidenceRepository.saveAndFlush(ammoEvidenceEntity);
        List<CaliberEntity> caliberEntities = caliberService.getCalibersList();
        ammoEvidenceEntity.setCaliberList(caliberEntities);
        ammoEvidenceRepository.saveAndFlush(ammoEvidenceEntity);
        LOG.info("Lista została utworzona");

    }

    public AmmoEvidenceEntity getAmmoEvidence() {

        if (ammoEvidenceRepository.findAll().isEmpty() || ammoEvidenceRepository.findAll() == null) {
            addAmmoEvidence();
        }
        LOG.info("Wyświetlono listę zużycia amunicji");
        return ammoEvidenceRepository.findAll().get(0);
    }

    public void addMemberToCaliber(UUID memberUUID, UUID caliberUUID, Integer quantity) {
        if (quantity != null) {
            caliberService.addAmmoUsedByMemberToCaliber(memberUUID, caliberUUID, quantity);
        }
    }

    public List<CaliberEntity> getCalibersList() {
        return caliberService.getCalibersList();
    }

    public Map<String, Integer> getMap(UUID memberUUID, UUID caliberUUID) {
        return caliberService.returnMap(memberUUID, caliberUUID);
    }
}
