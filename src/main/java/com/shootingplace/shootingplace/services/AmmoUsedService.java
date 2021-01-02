package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.AmmoUsedEntity;
import com.shootingplace.shootingplace.domain.entities.AmmoUsedToEvidenceEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.entities.PersonalEvidenceEntity;
import com.shootingplace.shootingplace.domain.models.AmmoUsedEvidence;
import com.shootingplace.shootingplace.domain.models.AmmoUsedPersonal;
import com.shootingplace.shootingplace.repositories.*;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
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

        MemberEntity memberEntity = memberRepository
                .findById(memberUUID)
                .orElseThrow(EntityNotFoundException::new);

        AmmoUsedPersonal ammoUsedPersonal = AmmoUsedPersonal.builder()
                .caliberName(name)
                .counter(quantity)
                .memberUUID(memberUUID)
                .caliberUUID(caliberUUID)
                .build();

        AmmoUsedEvidence ammoUsedEvidence = AmmoUsedEvidence.builder()
                .caliberName(name)
                .counter(quantity)
                .memberUUID(memberEntity)
                .caliberUUID(caliberUUID)
                .build();

        validateAmmo(ammoUsedPersonal);
        starEvidence(ammoUsedEvidence);
        return true;
    }

    private void validateAmmo(AmmoUsedPersonal ammoUsedpersonal) {
        PersonalEvidenceEntity personalEvidence = memberRepository
                .findById(ammoUsedpersonal.getMemberUUID())
                .orElseThrow(EntityNotFoundException::new)
                .getPersonalEvidence();

        boolean match = personalEvidence
                .getAmmoList()
                .stream()
                .filter(Objects::nonNull)
                .anyMatch(e -> e.getCaliberUUID()
                        .equals(ammoUsedpersonal.getCaliberUUID()) &&
                        e.getCaliberName()
                                .equals(ammoUsedpersonal.getCaliberName()));
        if (!match) {
            AmmoUsedEntity ammoUsedEntity = createAmmoUsedEntity(ammoUsedpersonal);
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
                    .filter(e -> e.getCaliberUUID().equals(ammoUsedpersonal.getCaliberUUID()))
                    .findFirst()
                    .orElseThrow(EntityNotFoundException::new);

            Integer counter = ammoUsedEntity.getCounter();

            ammoUsedEntity.setCounter(counter + ammoUsedpersonal.getCounter());
            if (ammoUsedEntity.getCounter() < 0) {
                ammoUsedEntity.setCounter(0);
            }

            ammoUsedRepository.saveAndFlush(ammoUsedEntity);
        }

    }

    private void starEvidence(AmmoUsedEvidence ammoUsedEvidence) {

        AmmoUsedToEvidenceEntity ammoUsedToEvidenceEntity = createAmmoUsedToEvidenceEntity(ammoUsedEvidence);

            ammoUsedToEvidenceEntityRepository.saveAndFlush(ammoUsedToEvidenceEntity);
            ammoInEvidenceService.addAmmoUsedEntityToAmmoInEvidenceEntity(ammoUsedToEvidenceEntity);

    }

    private AmmoUsedEntity createAmmoUsedEntity(AmmoUsedPersonal ammoUsedPersonal) {

        return ammoUsedRepository.saveAndFlush(Mapping.map(ammoUsedPersonal));

    }
//
//    public List<AmmoUsedEntity> getAllAmmoUsed() {
//
//        List<AmmoUsedEntity> list = ammoUsedRepository.findAll();
//        list.sort(Comparator.comparing(AmmoUsedEntity::getCaliberName));
//        return list;
//
//    }

    private AmmoUsedToEvidenceEntity createAmmoUsedToEvidenceEntity(AmmoUsedEvidence ammoUsedEvidence) {
        return ammoUsedToEvidenceEntityRepository.saveAndFlush(Mapping.map(ammoUsedEvidence));
    }

//    public List<AmmoUsedToEvidenceEntity> getAllsmth() {
//        return ammoUsedToEvidenceEntityRepository.findAll();
//    }
}
