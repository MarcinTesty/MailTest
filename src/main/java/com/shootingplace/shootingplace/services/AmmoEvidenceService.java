package com.shootingplace.shootingplace.services;

import com.itextpdf.text.DocumentException;
import com.shootingplace.shootingplace.domain.entities.AmmoEvidenceEntity;
import com.shootingplace.shootingplace.domain.entities.CaliberEntity;
import com.shootingplace.shootingplace.domain.models.FilesModel;
import com.shootingplace.shootingplace.repositories.AmmoEvidenceRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AmmoEvidenceService {

    private final CaliberService caliberService;
    private final FilesService filesService;
    private final AmmoEvidenceRepository ammoEvidenceRepository;
    private final Logger LOG = LogManager.getLogger(getClass());


    public AmmoEvidenceService(CaliberService caliberService, FilesService filesService, AmmoEvidenceRepository ammoEvidenceRepository) {
        this.caliberService = caliberService;
        this.filesService = filesService;
        this.ammoEvidenceRepository = ammoEvidenceRepository;
    }


    private void addAmmoEvidenceEntity() {
        AmmoEvidenceEntity ammoEvidenceEntity = AmmoEvidenceEntity.builder()
                .date(LocalDate.now())
                .label("Ewidencja zużycia amunicji")
                .caliberList(null)
                .file(null)
                .build();

        ammoEvidenceRepository.saveAndFlush(ammoEvidenceEntity);
        if (ammoEvidenceEntity.getFile() == null) {
            FilesModel filesModel = FilesModel.builder()
                    .name("")
                    .data(null)
                    .type(String.valueOf(MediaType.APPLICATION_PDF))
                    .build();
            filesService.createAmmoListFileEntity(ammoEvidenceEntity.getUuid(), filesModel);
            List<CaliberEntity> caliberEntities = caliberService.getCalibersList();
            ammoEvidenceEntity.setCaliberList(caliberEntities);
            ammoEvidenceRepository.saveAndFlush(ammoEvidenceEntity);
            LOG.info("Lista została utworzona");

        }
    }

    public AmmoEvidenceEntity getAmmoEvidence() throws IOException, DocumentException {

        if (ammoEvidenceRepository.findAll().isEmpty() || ammoEvidenceRepository.findAll() == null) {
            addAmmoEvidenceEntity();
            filesService.createAmmunitionListDocument(ammoEvidenceRepository.findAll().get(0).getUuid());

        }
        LOG.info("Wyświetlono listę zużycia amunicji");
        return ammoEvidenceRepository.findAll().get(0);
    }

    public void addMemberToCaliber(UUID memberUUID, UUID caliberUUID, Integer quantity) throws IOException, DocumentException {
        if (ammoEvidenceRepository.findAll().isEmpty() || ammoEvidenceRepository.findAll() == null){
            addAmmoEvidenceEntity();
        }
        if (quantity != null) {
            caliberService.addAmmoUsedByMemberToCaliber(memberUUID, caliberUUID, quantity);
        }
        filesService.createAmmunitionListDocument(ammoEvidenceRepository.findAll().get(0).getUuid());
    }

    public List<CaliberEntity> getCalibersList() {
        return caliberService.getCalibersList();
    }

    public Map<String, Integer> getMap(UUID memberUUID, UUID caliberUUID) {
        return caliberService.returnMap(memberUUID, caliberUUID);
    }
}
