package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.AmmoUsedEntity;
import com.shootingplace.shootingplace.domain.entities.AmmoUsedToEvidenceEntity;
import com.shootingplace.shootingplace.domain.entities.PersonalEvidenceEntity;
import com.shootingplace.shootingplace.domain.models.AmmoUsed;
import com.shootingplace.shootingplace.repositories.*;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class AmmoUsedService {
    private final PersonalEvidenceRepository personalEvidenceRepository;
    private final AmmoUsedToEvidenceEntityRepository ammoUsedToEvidenceEntityRepository;
    private final AmmoInEvidenceService ammoInEvidenceService;
    private final AmmoUsedRepository ammoUsedRepository;
    private final CaliberRepository caliberRepository;
    private final MemberRepository memberRepository;

    public AmmoUsedService(PersonalEvidenceRepository personalEvidenceRepository,
                           AmmoUsedToEvidenceEntityRepository ammoUsedToEvidenceEntityRepository,
                           AmmoInEvidenceService ammoInEvidenceService,
                           AmmoUsedRepository ammoUsedRepository,
                           CaliberRepository caliberRepository,
                           MemberRepository memberRepository) {
        this.personalEvidenceRepository = personalEvidenceRepository;
        this.ammoUsedToEvidenceEntityRepository = ammoUsedToEvidenceEntityRepository;
        this.ammoInEvidenceService = ammoInEvidenceService;
        this.ammoUsedRepository = ammoUsedRepository;
        this.caliberRepository = caliberRepository;
        this.memberRepository = memberRepository;
    }

    public boolean addAmmoUsedEntity(UUID caliberUUID, UUID memberUUID, Integer quantity) {

        String name = caliberRepository
                .findById(caliberUUID)
                .orElseThrow(EntityNotFoundException::new)
                .getName();


        AmmoUsed ammoUsed = AmmoUsed.builder()
                .caliberName(name)
                .counter(quantity)
                .memberUUID(memberUUID)
                .caliberUUID(caliberUUID)
                .build();

        validateAmmo(ammoUsed);
        starEvidence(ammoUsed);
        return true;
    }

    private void validateAmmo(AmmoUsed ammoUsed) {
        PersonalEvidenceEntity personalEvidence = memberRepository
                .findById(ammoUsed.getMemberUUID())
                .orElseThrow(EntityNotFoundException::new)
                .getPersonalEvidence();

        boolean match = personalEvidence
                .getAmmoList()
                .stream()
                .filter(Objects::nonNull)
                .anyMatch(e -> e.getCaliberUUID()
                        .equals(ammoUsed.getCaliberUUID()) &&
                        e.getCaliberName()
                                .equals(ammoUsed.getCaliberName()));
        if (!match) {
            AmmoUsedEntity ammoUsedEntity = createAmmoUsedEntity(ammoUsed);
            if (ammoUsedEntity.getCounter() < 0) {
                ammoUsedEntity.setCounter(0);
            }
            ammoUsedRepository.saveAndFlush(ammoUsedEntity);
            personalEvidence.getAmmoList().add(ammoUsedEntity);
            personalEvidence.getAmmoList().sort(Comparator.comparing(AmmoUsedEntity::getCaliberName));
            personalEvidenceRepository.saveAndFlush(personalEvidence);
        } else {
            AmmoUsedEntity ammoUsedEntity = personalEvidence
                    .getAmmoList()
                    .stream()
                    .filter(e -> e.getCaliberUUID().equals(ammoUsed.getCaliberUUID()))
                    .findFirst()
                    .orElseThrow(EntityNotFoundException::new);

            Integer counter = ammoUsedEntity.getCounter();

            ammoUsedEntity.setCounter(counter + ammoUsed.getCounter());
            if (ammoUsedEntity.getCounter() < 0) {
                ammoUsedEntity.setCounter(0);
            }

            ammoUsedRepository.saveAndFlush(ammoUsedEntity);
        }

    }

    private void starEvidence(AmmoUsed ammoUsed) {

        AmmoUsedToEvidenceEntity ammoUsedToEvidenceEntity = createAmmoUsedToEvidenceEntity(ammoUsed);

            System.out.println("zapis");
            ammoUsedToEvidenceEntityRepository.saveAndFlush(ammoUsedToEvidenceEntity);
            ammoInEvidenceService.addAmmoUsedEntityToAmmoInEvidenceEntity(ammoUsedToEvidenceEntity, ammoUsed.getCounter());


    }


    private AmmoUsedEntity createAmmoUsedEntity(AmmoUsed ammoUsed) {

        return ammoUsedRepository.saveAndFlush(Mapping.map(ammoUsed));

    }

    public List<AmmoUsedEntity> getAllAmmoUsed() {

        List<AmmoUsedEntity> list = ammoUsedRepository.findAll();
        list.sort(Comparator.comparing(AmmoUsedEntity::getCaliberName));
        return list;

    }

    private AmmoUsedToEvidenceEntity createAmmoUsedToEvidenceEntity(AmmoUsed ammoUsed) {
        return ammoUsedToEvidenceEntityRepository.saveAndFlush(Mapping.map1(ammoUsed));
    }

    public List<AmmoUsedToEvidenceEntity> getAllsmth() {
        return ammoUsedToEvidenceEntityRepository.findAll();
    }
}
