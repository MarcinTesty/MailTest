package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.AmmoEvidenceEntity;
import com.shootingplace.shootingplace.domain.entities.AmmoInEvidenceEntity;
import com.shootingplace.shootingplace.domain.entities.CaliberEntity;
import com.shootingplace.shootingplace.domain.entities.CaliberUsedEntity;
import com.shootingplace.shootingplace.repositories.AmmoEvidenceRepository;
import com.shootingplace.shootingplace.repositories.ArmoryRepository;
import com.shootingplace.shootingplace.repositories.CaliberRepository;
import com.shootingplace.shootingplace.repositories.CaliberUsedRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArmoryService {

    private final AmmoEvidenceRepository ammoEvidenceRepository;
    private final ArmoryRepository armoryRepository;
    private final CaliberRepository caliberRepository;
    private final CaliberUsedRepository caliberUsedRepository;

    public ArmoryService(AmmoEvidenceRepository ammoEvidenceRepository, ArmoryRepository armoryRepository, CaliberRepository caliberRepository, CaliberUsedRepository caliberUsedRepository) {
        this.ammoEvidenceRepository = ammoEvidenceRepository;
        this.armoryRepository = armoryRepository;
        this.caliberRepository = caliberRepository;
        this.caliberUsedRepository = caliberUsedRepository;
    }


    public List<AmmoEvidenceEntity> getSumFromAllAmmoList(LocalDate firstDate, LocalDate secondDate) {

        List<CaliberUsedEntity> all1 = caliberUsedRepository.findAll();

        all1.forEach(e -> {

            CaliberEntity caliberEntity = caliberRepository.findById(e.getBelongTo()).orElseThrow(EntityNotFoundException::new);


        });

        List<AmmoEvidenceEntity> all = ammoEvidenceRepository.findAll().stream().filter(f -> f.getDate().isAfter(firstDate) && f.getDate().isBefore(secondDate)).collect(Collectors.toList());
        all.sort(Comparator.comparing(AmmoEvidenceEntity::getDate).reversed());
        List<AmmoInEvidenceEntity> ammo = new ArrayList<>();
        all.forEach(e -> e.getAmmoInEvidenceEntityList().forEach(f -> ammo.add(f)));
        return all;


    }

    public List<CaliberUsedEntity> update() {

        List<CaliberUsedEntity> all = caliberUsedRepository.findAll();

        return all;
//        return "udało się";
    }

    public String update2() {

        List<AmmoEvidenceEntity> all = ammoEvidenceRepository.findAll();
        if (caliberUsedRepository.findAll().isEmpty()) {
            all.forEach(e -> e.getAmmoInEvidenceEntityList().forEach(f ->
                    {
                        CaliberUsedEntity caliberUsedEntity = CaliberUsedEntity.builder()
                                .ammoUsed(f.getQuantity())
                                .belongTo(f.getCaliberUUID())
                                .date(e.getDate())
                                .build();

                        caliberUsedRepository.save(caliberUsedEntity);
                        CaliberEntity caliberEntity = caliberRepository.findById(f.getCaliberUUID()).orElseThrow(EntityNotFoundException::new);
                        List<CaliberUsedEntity> ammoUsed = caliberEntity.getAmmoUsed();
                        System.out.println(caliberEntity.getQuantity()-caliberUsedEntity.getAmmoUsed());
                        caliberEntity.setQuantity(caliberEntity.getQuantity()-caliberUsedEntity.getAmmoUsed());
                        caliberRepository.save(caliberEntity);
                    }
            ));

//            LocalDate date = all.get(0).getDate();
//            String caliberUUID = all.get(0).getAmmoInEvidenceEntityList().get(0).getCaliberUUID();
//            Integer quantity = all.get(0).getAmmoInEvidenceEntityList().get(0).getQuantity();
//            CaliberUsedEntity caliberUsedEntity = CaliberUsedEntity.builder()
//                    .ammoUsed(quantity)
//                    .belongTo(caliberUUID)
//                    .date(date)
//                    .build();
//
//            caliberUsedRepository.save(caliberUsedEntity);
//
//            List<CaliberEntity> all1 = caliberRepository.findAll();
//            all1.forEach(e->{
//                List<CaliberUsedEntity> ammoUsed = e.getAmmoUsed();
//                ammoUsed.add();
//            });

            System.out.println("udało się");
            return "udało się";
        }
        System.out.println("nie udało się");
        return "nie udało się";
    }


}
