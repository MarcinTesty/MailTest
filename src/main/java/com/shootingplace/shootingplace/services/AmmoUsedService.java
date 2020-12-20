package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.AmmoUsedEntity;
import com.shootingplace.shootingplace.domain.entities.PersonalEvidenceEntity;
import com.shootingplace.shootingplace.domain.models.AmmoUsed;
import com.shootingplace.shootingplace.repositories.AmmoUsedRepository;
import com.shootingplace.shootingplace.repositories.CaliberRepository;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import com.shootingplace.shootingplace.repositories.PersonalEvidenceRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class AmmoUsedService {
    private final PersonalEvidenceRepository personalEvidenceRepository;
    private final AmmoUsedRepository ammoUsedRepository;
    private final CaliberRepository caliberRepository;
    private final MemberRepository memberRepository;

    public AmmoUsedService(PersonalEvidenceRepository personalEvidenceRepository, AmmoUsedRepository ammoUsedRepository, CaliberRepository caliberRepository, MemberRepository memberRepository) {
        this.personalEvidenceRepository = personalEvidenceRepository;
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
                .member(memberUUID)
                .caliberUUID(caliberUUID)
                .build();

        validateAmmo(ammoUsed);
        return true;
    }

    public List<AmmoUsedEntity> getAllAmmoUsed() {

        List<AmmoUsedEntity> list = ammoUsedRepository.findAll();
        list.sort(Comparator.comparing(AmmoUsedEntity::getCaliberName));
        return list;

    }

    private void validateAmmo(AmmoUsed ammoUsed) {

        PersonalEvidenceEntity personalEvidence = memberRepository
                .findById(ammoUsed.getMember())
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

            AmmoUsedEntity ammoUsedEntity = personalEvidence.getAmmoList().stream().filter(e -> e.getCaliberUUID().equals(ammoUsed.getCaliberUUID())).findFirst().orElseThrow(EntityNotFoundException::new);

            Integer counter = ammoUsedEntity.getCounter();

            ammoUsedEntity.setCounter(counter + ammoUsed.getCounter());
            if (ammoUsedEntity.getCounter() < 0) {
                ammoUsedEntity.setCounter(0);
            }
            ammoUsedRepository.saveAndFlush(ammoUsedEntity);
        }

    }

    private AmmoUsedEntity createAmmoUsedEntity(AmmoUsed ammoUsed) {

        return ammoUsedRepository.saveAndFlush(Mapping.map(ammoUsed));

    }
}
