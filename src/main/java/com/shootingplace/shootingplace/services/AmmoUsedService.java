package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.*;
import com.shootingplace.shootingplace.domain.models.AmmoUsedEvidence;
import com.shootingplace.shootingplace.domain.models.AmmoUsedPersonal;
import com.shootingplace.shootingplace.repositories.*;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.Objects;

@Service
public class AmmoUsedService {
    private final PersonalEvidenceRepository personalEvidenceRepository;
    private final AmmoUsedToEvidenceEntityRepository ammoUsedToEvidenceEntityRepository;
    private final AmmoInEvidenceService ammoInEvidenceService;
    private final AmmoUsedRepository ammoUsedRepository;
    private final CaliberRepository caliberRepository;
    private final MemberRepository memberRepository;
    private final OtherPersonRepository otherPersonRepository;

    public AmmoUsedService(PersonalEvidenceRepository personalEvidenceRepository,
                           AmmoUsedToEvidenceEntityRepository ammoUsedToEvidenceEntityRepository,
                           AmmoInEvidenceService ammoInEvidenceService,
                           AmmoUsedRepository ammoUsedRepository,
                           CaliberRepository caliberRepository,
                           MemberRepository memberRepository, OtherPersonRepository otherPersonRepository) {
        this.personalEvidenceRepository = personalEvidenceRepository;
        this.ammoUsedToEvidenceEntityRepository = ammoUsedToEvidenceEntityRepository;
        this.ammoInEvidenceService = ammoInEvidenceService;
        this.ammoUsedRepository = ammoUsedRepository;
        this.caliberRepository = caliberRepository;
        this.memberRepository = memberRepository;
        this.otherPersonRepository = otherPersonRepository;
    }

    public boolean addAmmoUsedEntity(String caliberUUID, Integer legitimationNumber, int otherID, Integer quantity) {

        String caliberName = caliberRepository
                .findById(caliberUUID)
                .orElseThrow(EntityNotFoundException::new)
                .getName();


        String name;
        if (legitimationNumber > 0) {
            MemberEntity memberEntity = memberRepository.findAll().stream().filter(f -> f.getLegitimationNumber().equals(legitimationNumber)).findFirst().orElseThrow(EntityNotFoundException::new);
            name = memberEntity.getSecondName() + " " + memberEntity.getFirstName();
            AmmoUsedPersonal ammoUsedPersonal = AmmoUsedPersonal.builder()
                    .caliberName(caliberName)
                    .counter(quantity)
                    .memberUUID(memberEntity.getUuid())
                    .caliberUUID(caliberUUID)
                    .build();


            AmmoUsedEvidence ammoUsedEvidence = AmmoUsedEvidence.builder()
                    .caliberName(caliberName)
                    .counter(quantity)
                    .memberEntity(memberEntity)
                    .otherPersonEntity(null)
                    .userName(name)
                    .caliberUUID(caliberUUID)
                    .build();
            validateAmmo(ammoUsedPersonal);
            return starEvidence(ammoUsedEvidence);
//            return true;

        }
        if (otherID > 0) {

            OtherPersonEntity otherPersonEntity = otherPersonRepository
                    .findById(otherID)
                    .orElseThrow(EntityNotFoundException::new);
            name = otherPersonEntity.getSecondName() + " " + otherPersonEntity.getFirstName();


            AmmoUsedEvidence ammoUsedEvidence = AmmoUsedEvidence.builder()
                    .caliberName(caliberName)
                    .counter(quantity)
                    .memberEntity(null)
                    .otherPersonEntity(otherPersonEntity)
                    .userName(name)
                    .caliberUUID(caliberUUID)
                    .build();
            return starEvidence(ammoUsedEvidence);
//            return true;

        }

        return false;
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

    private boolean starEvidence(AmmoUsedEvidence ammoUsedEvidence) {

        AmmoUsedToEvidenceEntity ammoUsedToEvidenceEntity = createAmmoUsedToEvidenceEntity(ammoUsedEvidence);

        ammoUsedToEvidenceEntityRepository.saveAndFlush(ammoUsedToEvidenceEntity);
        return ammoInEvidenceService.addAmmoUsedEntityToAmmoInEvidenceEntity(ammoUsedToEvidenceEntity);

    }

    private AmmoUsedEntity createAmmoUsedEntity(AmmoUsedPersonal ammoUsedPersonal) {

        return ammoUsedRepository.saveAndFlush(Mapping.map(ammoUsedPersonal));

    }

    private AmmoUsedToEvidenceEntity createAmmoUsedToEvidenceEntity(AmmoUsedEvidence ammoUsedEvidence) {
        return ammoUsedToEvidenceEntityRepository.saveAndFlush(Mapping.map(ammoUsedEvidence));
    }

}
