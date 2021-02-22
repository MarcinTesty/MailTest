package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.*;
import com.shootingplace.shootingplace.domain.enums.GunType;
import com.shootingplace.shootingplace.domain.models.Caliber;
import com.shootingplace.shootingplace.repositories.*;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArmoryService {

    private final AmmoEvidenceRepository ammoEvidenceRepository;
    private final CaliberRepository caliberRepository;
    private final CaliberUsedRepository caliberUsedRepository;
    private final CalibersAddedRepository calibersAddedRepository;
    private final GunRepository gunRepository;

    public ArmoryService(AmmoEvidenceRepository ammoEvidenceRepository, CaliberRepository caliberRepository, CaliberUsedRepository caliberUsedRepository, CalibersAddedRepository calibersAddedRepository, GunRepository gunRepository) {
        this.ammoEvidenceRepository = ammoEvidenceRepository;
        this.caliberRepository = caliberRepository;
        this.caliberUsedRepository = caliberUsedRepository;
        this.calibersAddedRepository = calibersAddedRepository;
        this.gunRepository = gunRepository;
    }


    public List<Caliber> getSumFromAllAmmoList(LocalDate firstDate, LocalDate secondDate) {
        List<Caliber> list = new ArrayList<>();
        List<CaliberEntity> calibersList = caliberRepository.findAll();
        calibersList.forEach(e ->
                list.add(Mapping.map(e))
        );

        List<Caliber> list1 = new ArrayList<>();
        list.forEach(e -> ammoEvidenceRepository.findAll().stream()
                .filter(f -> f.getDate().isAfter(firstDate.minusDays(1)))
                .filter(f -> f.getDate().isBefore(secondDate.plusDays(1)))
                .forEach(g -> g.getAmmoInEvidenceEntityList()
                        .stream().filter(f -> f.getCaliberName().equals(e.getName()))
                        .forEach(h -> {
                            Caliber caliber = list.stream()
                                    .filter(f -> f.getName().equals(e.getName())).findFirst().orElseThrow(EntityNotFoundException::new);
                            if (caliber.getQuantity() == null) {
                                caliber.setQuantity(0);
                            }
                            if (list1.stream().anyMatch(f -> f.getName().equals(caliber.getName()))) {
                                Caliber caliber1 = list1.stream().filter(f -> f.getName().equals(caliber.getName())).findFirst().orElseThrow(EntityNotFoundException::new);
                                caliber.setQuantity(caliber1.getQuantity() + h.getQuantity());
                            } else {

                                caliber.setQuantity(h.getQuantity());
                                list1.add(caliber);
                            }

                        })
                ));

        return list1;


    }

    public String update() {
        List<AmmoEvidenceEntity> all = ammoEvidenceRepository.findAll();
        if (caliberUsedRepository.findAll().isEmpty()) {
            System.out.println(2);
            all.forEach(e -> e.getAmmoInEvidenceEntityList().forEach(f ->
                    {
                        System.out.println(3);
                        CaliberUsedEntity caliberUsedEntity = CaliberUsedEntity.builder()
                                .ammoUsed(f.getQuantity())
                                .belongTo(f.getCaliberUUID())
                                .date(e.getDate())
                                .build();

                        caliberUsedRepository.save(caliberUsedEntity);
                        CaliberEntity caliberEntity = caliberRepository.findById(f.getCaliberUUID()).orElseThrow(EntityNotFoundException::new);
                        caliberEntity.setQuantity(caliberEntity.getQuantity() - caliberUsedEntity.getAmmoUsed());
                        caliberRepository.save(caliberEntity);
                    }
            ));


            return "udało się";
        }
        return "nie udało się";
    }


    public void updateAmmo(String caliberUUID, Integer count, LocalDate date, String description) {
        CaliberEntity caliberEntity = caliberRepository.findById(caliberUUID).orElseThrow(EntityNotFoundException::new);
        if (caliberEntity.getQuantity() == null) {
            caliberEntity.setQuantity(0);
        }

        CalibersAddedEntity calibersAddedEntity = CalibersAddedEntity.builder()
                .ammoAdded(count)
                .belongTo(caliberUUID)
                .date(date)
                .description(description)
                .stateForAddedDay(caliberEntity.getQuantity())
                .finalStateForAddedDay(caliberEntity.getQuantity() + count)
                .build();
        calibersAddedRepository.save(calibersAddedEntity);

        List<CalibersAddedEntity> ammoAdded = caliberEntity.getAmmoAdded();
        ammoAdded.add(calibersAddedEntity);
        if (caliberEntity.getQuantity() == null) {
            caliberEntity.setQuantity(0);
        }
        caliberEntity.setQuantity(caliberEntity.getQuantity() + calibersAddedEntity.getAmmoAdded());

        caliberRepository.save(caliberEntity);

    }

    public void substratAmmo(String caliberUUID, Integer quantity) {

        CaliberEntity caliberEntity = caliberRepository.findById(caliberUUID).orElseThrow(EntityNotFoundException::new);
        if (caliberEntity.getQuantity() - quantity < 0) {
            throw new IllegalArgumentException();
        }
        CaliberUsedEntity caliberUsedEntity = CaliberUsedEntity.builder()
                .date(LocalDate.now())
                .belongTo(caliberUUID)
                .ammoUsed(quantity)
                .build();
        caliberUsedRepository.saveAndFlush(caliberUsedEntity);

        List<CaliberUsedEntity> ammoUsed = caliberEntity.getAmmoUsed();

        ammoUsed.add(caliberUsedEntity);
        caliberEntity.setQuantity(caliberEntity.getQuantity() - caliberUsedEntity.getAmmoUsed());
        caliberRepository.saveAndFlush(caliberEntity);

    }

    public List<CalibersAddedEntity> getHistoryOfCaliber(String caliberUUID) {
        CaliberEntity caliberEntity = caliberRepository.findById(caliberUUID).orElseThrow(EntityNotFoundException::new);
        List<CalibersAddedEntity> ammoAdded = caliberEntity.getAmmoAdded();
        ammoAdded.sort(Comparator.comparing(CalibersAddedEntity::getDate));
        return ammoAdded;

    }

    public boolean addGunEntity(String modelName,
                                String caliber,
                                String gunType,
                                String serialNumber,
                                String productionYear,
                                String numberOfMagazines,
                                String gunCertificateSerialNumber,
                                String additionalEquipment,
                                String recordInEvidenceBook,
                                String comment,
                                String basisForPurchaseOrAssignment) {

        if(modelName.isEmpty()||caliber.isEmpty()||gunType.isEmpty()||serialNumber.isEmpty()||gunCertificateSerialNumber.isEmpty()||recordInEvidenceBook.isEmpty()){
            System.out.println("nie udało się dodać broni");
            return false;
        }
        if(productionYear.isEmpty()){
            productionYear = null;
        }
        if(additionalEquipment.isEmpty()){
            additionalEquipment = null;
        }
        if(comment.isEmpty()){
            comment = null;
        }

        List<GunEntity> all = gunRepository.findAll();

        if (all.stream().anyMatch(e -> e.getGunCertificateSerialNumber().equals(gunCertificateSerialNumber) || e.getSerialNumber().equals(serialNumber) || e.getRecordInEvidenceBook().equals(recordInEvidenceBook))) {
            System.out.println("nie udało się dodać broni");
            return false;
        } else {

            GunEntity gunEntity = GunEntity.builder()
                    .modelName(modelName.toUpperCase())
                    .caliber(caliber)
                    .gunType(gunType)
                    .serialNumber(serialNumber.toUpperCase())
                    .productionYear(productionYear)
                    .numberOfMagazines(numberOfMagazines)
                    .gunCertificateSerialNumber(gunCertificateSerialNumber.toUpperCase())
                    .additionalEquipment(additionalEquipment)
                    .recordInEvidenceBook(recordInEvidenceBook)
                    .comment(comment)
                    .basisForPurchaseOrAssignment(basisForPurchaseOrAssignment)
                    .inStock(true).build();

            gunRepository.saveAndFlush(gunEntity);

            System.out.println("Dodano nową broń");

            return true;
        }
    }

    public List<String> getGunTypeList() {
        List<String> list = new ArrayList<>();

        GunType[] values = GunType.values();

        Arrays.stream(values).forEach(e -> list.add(e.getName()));
        return list;
    }

    public List<GunEntity> getAllGuns() {
        List<GunEntity> all = gunRepository.findAll();
        return all.stream().filter(GunEntity::isInStock).sorted(Comparator.comparing(GunEntity::getCaliber).thenComparing(GunEntity::getModelName)).collect(Collectors.toList());
    }
}
