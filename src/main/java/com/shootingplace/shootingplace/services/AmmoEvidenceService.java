package com.shootingplace.shootingplace.services;

import com.itextpdf.text.DocumentException;
import com.shootingplace.shootingplace.domain.entities.AmmoEvidenceEntity;
import com.shootingplace.shootingplace.domain.entities.CaliberEntity;
import com.shootingplace.shootingplace.domain.models.FilesModel;
import com.shootingplace.shootingplace.repositories.AmmoEvidenceRepository;
import com.shootingplace.shootingplace.repositories.AmmoInEvidenceRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AmmoEvidenceService {

    private final AmmoInEvidenceRepository ammoInEvidenceRepository;

    private final CaliberService caliberService;
    private final FilesService filesService;
    private final AmmoEvidenceRepository ammoEvidenceRepository;
    private final Logger LOG = LogManager.getLogger(getClass());


    public AmmoEvidenceService(AmmoInEvidenceRepository ammoInEvidenceRepository, CaliberService caliberService, FilesService filesService, AmmoEvidenceRepository ammoEvidenceRepository) {
        this.ammoInEvidenceRepository = ammoInEvidenceRepository;
        this.caliberService = caliberService;
        this.filesService = filesService;
        this.ammoEvidenceRepository = ammoEvidenceRepository;
    }


    private void addAmmoEvidenceEntity() {
        AmmoEvidenceEntity ammoEvidenceEntity = AmmoEvidenceEntity.builder()
                .date(LocalDate.now())
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
        if (ammoEvidenceRepository.findAll().isEmpty() || ammoEvidenceRepository.findAll() == null) {
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

//    public Map<String, Integer> getMap(UUID memberUUID, UUID caliberUUID) {
//        return caliberService.returnMap(memberUUID, caliberUUID);
//    }

//    void addAmmoInEvidenceToAmmoEvidence(AmmoInEvidenceEntity ammoInEvidenceEntity) {
//
//        if (ammoEvidenceRepository.findAll().size() < 1) {
//            AmmoEvidenceEntity ammoEvidenceEntity = AmmoEvidenceEntity.builder()
//                    .date(LocalDate.now())
//                    .number("1/01/2021")
//                    .open(true)
//                    .ammoInEvidenceEntityList(new ArrayList<>())
//                    .build();
//            ammoEvidenceRepository.saveAndFlush(ammoEvidenceEntity);
//            ammoInEvidenceEntity.setEvidenceUUID(ammoEvidenceEntity.getUuid());
//            ammoInEvidenceRepository.saveAndFlush(ammoInEvidenceEntity);
//            ammoEvidenceEntity.getAmmoInEvidenceEntityList().add(ammoInEvidenceEntity);
//            ammoEvidenceRepository.saveAndFlush(ammoEvidenceEntity);
//        } else {
//
//            AmmoEvidenceEntity ammoEvidenceEntity = ammoEvidenceRepository
//                    .findAll()
//                    .stream()
//                    .findFirst()
//                    .orElseThrow(EntityNotFoundException::new);
//            ammoEvidenceEntity.getAmmoInEvidenceEntityList().add(ammoInEvidenceEntity);
//            ammoEvidenceRepository.saveAndFlush(ammoEvidenceEntity);
//
//        }
//    }

    public List<AmmoEvidenceEntity> getAllEvidences(boolean state) {
        List<AmmoEvidenceEntity> collect = ammoEvidenceRepository.findAll().stream().filter(f -> f.isOpen() == state).collect(Collectors.toList());
        collect.sort(Comparator.comparing(AmmoEvidenceEntity::getDate).reversed());
        return collect;
    }

    public boolean closeEvidence(UUID evidenceUUID) {
        AmmoEvidenceEntity ammoEvidenceEntity = ammoEvidenceRepository
                .findById(evidenceUUID)
                .orElseThrow(EntityNotFoundException::new);
        ammoEvidenceEntity.setOpen(false);
        ammoEvidenceRepository.saveAndFlush(ammoEvidenceEntity);
        System.out.println("zamknięto");
        return true;
    }
}
