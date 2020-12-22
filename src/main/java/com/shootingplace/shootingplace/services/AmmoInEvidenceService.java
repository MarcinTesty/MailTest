package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.AmmoEvidenceEntity;
import com.shootingplace.shootingplace.domain.entities.AmmoInEvidenceEntity;
import com.shootingplace.shootingplace.domain.entities.AmmoUsedToEvidenceEntity;
import com.shootingplace.shootingplace.repositories.AmmoEvidenceRepository;
import com.shootingplace.shootingplace.repositories.AmmoInEvidenceRepository;
import com.shootingplace.shootingplace.repositories.AmmoUsedToEvidenceEntityRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AmmoInEvidenceService {
    private final AmmoInEvidenceRepository ammoInEvidenceRepository;
    private final AmmoEvidenceService ammoEvidenceService;
    private final AmmoUsedToEvidenceEntityRepository ammoUsedToEvidenceEntityRepository;

    private final AmmoEvidenceRepository ammoEvidenceRepository;

    public AmmoInEvidenceService(AmmoInEvidenceRepository ammoInEvidenceRepository, AmmoEvidenceService ammoEvidenceService, AmmoUsedToEvidenceEntityRepository ammoUsedToEvidenceEntityRepository, AmmoEvidenceRepository ammoEvidenceRepository) {
        this.ammoInEvidenceRepository = ammoInEvidenceRepository;
        this.ammoEvidenceService = ammoEvidenceService;
        this.ammoUsedToEvidenceEntityRepository = ammoUsedToEvidenceEntityRepository;
        this.ammoEvidenceRepository = ammoEvidenceRepository;
    }

    void addAmmoUsedEntityToAmmoInEvidenceEntity(AmmoUsedToEvidenceEntity ammoUsedToEvidenceEntity, Integer counter) {

//      Nie znaleziono żadnej listy
        if (ammoEvidenceRepository.findAll().size() < 1 || ammoEvidenceRepository.findAll() == null) {
            System.out.println("tworzymy od zera");
            AmmoEvidenceEntity buildEvidence = AmmoEvidenceEntity.builder()
                    .date(LocalDate.now())
                    .open(true)
                    .ammoInEvidenceEntityList(new ArrayList<>())
                    .number("1")
                    .build();
            ammoEvidenceRepository.saveAndFlush(buildEvidence);

            AmmoInEvidenceEntity build = AmmoInEvidenceEntity.builder()
                    .caliberName(ammoUsedToEvidenceEntity.getCaliberName())
                    .caliberUUID(ammoUsedToEvidenceEntity.getCaliberUUID())
                    .evidenceUUID(buildEvidence.getUuid())
                    .quantity(0)
                    .ammoUsedToEvidenceEntityList(new ArrayList<>())
                    .build();

            build.getAmmoUsedToEvidenceEntityList().add(ammoUsedToEvidenceEntity);
            build.setQuantity(ammoUsedToEvidenceEntity.getCounter());
            ammoInEvidenceRepository.saveAndFlush(build);

            buildEvidence.getAmmoInEvidenceEntityList().add(build);
            ammoEvidenceRepository.saveAndFlush(buildEvidence);


        }
//      Znaleziono jakąś otwartą listę
        else {
            System.out.println("jest ewidencja");
            AmmoEvidenceEntity ammoEvidenceEntity = ammoEvidenceRepository
                    .findAll()
                    .stream()
                    .filter(AmmoEvidenceEntity::isOpen)
                    .findFirst()
                    .orElseThrow(EntityNotFoundException::new);

            List<AmmoInEvidenceEntity> ammoInEvidenceEntityList = ammoEvidenceEntity.getAmmoInEvidenceEntityList();
//       Znaleziono jakąś listę z podanym kalibrem w ewidencji
            if (ammoInEvidenceEntityList
                    .stream()
                    .anyMatch(a -> a.getCaliberUUID().equals(ammoUsedToEvidenceEntity.getCaliberUUID()))) {

                System.out.println("jest lista z kalibrem");

                AmmoInEvidenceEntity ammoInEvidenceEntity = ammoEvidenceEntity.getAmmoInEvidenceEntityList()
                        .stream()
                        .filter(f -> f.getCaliberUUID().equals(ammoUsedToEvidenceEntity.getCaliberUUID()))
                        .findFirst()
                        .orElseThrow(EntityNotFoundException::new);

                List<AmmoUsedToEvidenceEntity> ammoUsedToEvidenceEntityList = ammoInEvidenceEntity.getAmmoUsedToEvidenceEntityList();
//        Znaleziono podanego membera
                if (ammoUsedToEvidenceEntityList.stream().anyMatch(f -> f.getMemberUUID().equals(ammoUsedToEvidenceEntity.getMemberUUID()))) {
                    System.out.println("jest member");
                    AmmoUsedToEvidenceEntity ammoUsedToEvidenceEntity1 = ammoUsedToEvidenceEntityList
                            .stream()
                            .filter(f -> f.getMemberUUID()
                                    .equals(ammoUsedToEvidenceEntity.getMemberUUID()))
                            .findFirst()
                            .orElseThrow(EntityNotFoundException::new);
                    ammoInEvidenceEntity.setQuantity(ammoInEvidenceEntity.getQuantity() + ammoUsedToEvidenceEntity.getCounter());
                    ammoUsedToEvidenceEntity1.setCounter(ammoUsedToEvidenceEntity1.getCounter()+ammoUsedToEvidenceEntity.getCounter());
                    ammoUsedToEvidenceEntityRepository.saveAndFlush(ammoUsedToEvidenceEntity1);
                    ammoInEvidenceRepository.saveAndFlush(ammoInEvidenceEntity);
                }
//        Nie znaleziono podanego membera
                else {
                    System.out.println("nie ma membera");
                    ammoUsedToEvidenceEntityList.add(ammoUsedToEvidenceEntity);
                    ammoInEvidenceEntity.setQuantity(ammoInEvidenceEntity.getQuantity() + ammoUsedToEvidenceEntity.getCounter());

                    ammoInEvidenceRepository.saveAndFlush(ammoInEvidenceEntity);

                }

            }
//       Nie znaleziono żadnej listy z podanym kalibrem
            else {
                System.out.println("nie ma listy z kalibrem");
                AmmoInEvidenceEntity build = AmmoInEvidenceEntity.builder()
                        .caliberName(ammoUsedToEvidenceEntity.getCaliberName())
                        .caliberUUID(ammoUsedToEvidenceEntity.getCaliberUUID())
                        .evidenceUUID(ammoEvidenceEntity.getUuid())
                        .quantity(0)
                        .ammoUsedToEvidenceEntityList(new ArrayList<>())
                        .build();

                build.getAmmoUsedToEvidenceEntityList().add(ammoUsedToEvidenceEntity);
                build.setQuantity(ammoUsedToEvidenceEntity.getCounter());
                ammoInEvidenceRepository.saveAndFlush(build);
            }

        }

    }


    public List<AmmoInEvidenceEntity> getAllAmmoInEvidence() {
        return ammoInEvidenceRepository.findAll();
    }

}
