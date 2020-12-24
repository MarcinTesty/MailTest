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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AmmoInEvidenceService {
    private final AmmoInEvidenceRepository ammoInEvidenceRepository;
    private final AmmoUsedToEvidenceEntityRepository ammoUsedToEvidenceEntityRepository;

    private final AmmoEvidenceRepository ammoEvidenceRepository;

    public AmmoInEvidenceService(AmmoInEvidenceRepository ammoInEvidenceRepository, AmmoUsedToEvidenceEntityRepository ammoUsedToEvidenceEntityRepository, AmmoEvidenceRepository ammoEvidenceRepository) {
        this.ammoInEvidenceRepository = ammoInEvidenceRepository;
        this.ammoUsedToEvidenceEntityRepository = ammoUsedToEvidenceEntityRepository;
        this.ammoEvidenceRepository = ammoEvidenceRepository;
    }

    void addAmmoUsedEntityToAmmoInEvidenceEntity(AmmoUsedToEvidenceEntity ammoUsedToEvidenceEntity, Integer counter) {

        List<AmmoEvidenceEntity> collect = ammoEvidenceRepository.findAll().stream().filter(AmmoEvidenceEntity::isOpen).collect(Collectors.toList());


//      Nie znaleziono żadnej listy
        if (collect.size() < 1 || ammoEvidenceRepository.findAll() == null) {
            System.out.println("tworzymy od zera");
            if (ammoUsedToEvidenceEntity.getCounter() < 0) {
                System.out.println("nie można dodać ujemnej wartości");
            } else {
//                nadawanie numeru listy
                int number;
//                nadawanie numeru od zera
                if (ammoEvidenceRepository.findAll().size() < 1 || ammoEvidenceRepository.findAll() == null) {
                    number = 1;
                    System.out.println(number);
//                    nadawanie kolejnego numeru
                } else {
                    List<AmmoEvidenceEntity> all = ammoEvidenceRepository.findAll();
                    all.sort(Comparator.comparing(AmmoEvidenceEntity::getNumber).reversed());
                    String number1 = all.get(0).getNumber();
                    String[] split = number1.split("/");
                    number = Integer.valueOf(split[0])+1;
                }
                String evidenceNumber = number + "/LA/" + LocalDate.now().getYear();
                AmmoEvidenceEntity buildEvidence = AmmoEvidenceEntity.builder()
                        .date(LocalDate.now())
                        .open(true)
                        .ammoInEvidenceEntityList(new ArrayList<>())
                        .number(evidenceNumber)
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
//        Nie znaleziono podanego membera
                if (ammoUsedToEvidenceEntityList.stream().noneMatch(f -> f.getMemberUUID().equals(ammoUsedToEvidenceEntity.getMemberUUID()))) {
                    System.out.println("nie ma membera");
                    if (ammoUsedToEvidenceEntity.getCounter() < 0) {
                        System.out.println("nie można dodać ujemnej wartości");
                    } else {
                        ammoUsedToEvidenceEntityList.add(ammoUsedToEvidenceEntity);
                        ammoInEvidenceEntity.setQuantity(ammoInEvidenceEntity.getQuantity() + ammoUsedToEvidenceEntity.getCounter());

                        ammoInEvidenceRepository.saveAndFlush(ammoInEvidenceEntity);
                    }
                }
//        Znaleziono podanego membera
                else {
                    System.out.println("jest member");
                    AmmoUsedToEvidenceEntity ammoUsedToEvidenceEntity1 = ammoUsedToEvidenceEntityList
                            .stream()
                            .filter(f -> f.getMemberUUID()
                                    .equals(ammoUsedToEvidenceEntity.getMemberUUID()))
                            .findFirst()
                            .orElseThrow(EntityNotFoundException::new);

                    ammoInEvidenceEntity.setQuantity(ammoInEvidenceEntity.getQuantity() + ammoUsedToEvidenceEntity.getCounter());
                    ammoUsedToEvidenceEntity1.setCounter(ammoUsedToEvidenceEntity1.getCounter() + ammoUsedToEvidenceEntity.getCounter());
                    if (ammoUsedToEvidenceEntity1.getCounter() <= 0) {
                        ammoInEvidenceEntity.getAmmoUsedToEvidenceEntityList().remove(ammoUsedToEvidenceEntity1);
                    }
                    ammoUsedToEvidenceEntityRepository.saveAndFlush(ammoUsedToEvidenceEntity1);
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
                ammoEvidenceEntity.getAmmoInEvidenceEntityList().add(build);
                ammoEvidenceRepository.saveAndFlush(ammoEvidenceEntity);
            }

        }
//          Usuwanie listy jeśli ilość sztuk wynosi 0
        AmmoEvidenceEntity ammoEvidenceEntity = ammoEvidenceRepository
                .findAll()
                .stream()
                .findFirst()
                .orElseThrow(EntityNotFoundException::new);

        if (ammoEvidenceEntity
                .getAmmoInEvidenceEntityList()
                .stream()
                .filter(f -> f.getQuantity() <= 0)
                .anyMatch(a -> a.getQuantity() <= 0)) {

            AmmoInEvidenceEntity ammoInEvidenceEntity = ammoEvidenceEntity
                    .getAmmoInEvidenceEntityList()
                    .stream()
                    .filter(f -> f.getQuantity() <= 0)
                    .findFirst()
                    .orElseThrow(EntityNotFoundException::new);

            ammoEvidenceEntity.getAmmoInEvidenceEntityList().remove(ammoInEvidenceEntity);
            ammoEvidenceRepository.saveAndFlush(ammoEvidenceEntity);

        }
//        Usuwanie ewidencji jeśli nie ma żadnej listy z amunicją
        if (ammoEvidenceEntity.getAmmoInEvidenceEntityList().isEmpty()) {
            System.out.println("usuwam");
            ammoEvidenceRepository.deleteById(ammoEvidenceEntity.getUuid());
        }
    }


    public List<AmmoInEvidenceEntity> getAllAmmoInEvidence() {
        return ammoInEvidenceRepository.findAll();
    }

}
