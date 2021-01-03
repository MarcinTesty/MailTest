package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.entities.PersonalEvidenceEntity;
import com.shootingplace.shootingplace.domain.models.PersonalEvidence;
import com.shootingplace.shootingplace.repositories.CaliberRepository;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import com.shootingplace.shootingplace.repositories.PersonalEvidenceRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class PersonalEvidenceService {

    private final PersonalEvidenceRepository personalEvidenceRepository;
    private final CaliberRepository caliberRepository;
    private final MemberRepository memberRepository;
    private final Logger LOG = LogManager.getLogger(getClass());


    public PersonalEvidenceService(PersonalEvidenceRepository personalEvidenceRepository, CaliberRepository caliberRepository, MemberRepository memberRepository) {
        this.personalEvidenceRepository = personalEvidenceRepository;
        this.caliberRepository = caliberRepository;
        this.memberRepository = memberRepository;
    }

    void addPersonalEvidence(UUID memberUUID, PersonalEvidence personalEvidence) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        if (memberEntity.getPersonalEvidence() != null) {
            LOG.error("nie można już dodać pola");
        }
        PersonalEvidenceEntity personalEvidenceEntity = Mapping.map(personalEvidence);
        personalEvidenceEntity.setAmmoList(new ArrayList<>());
        personalEvidenceRepository.saveAndFlush(personalEvidenceEntity);
        memberEntity.setPersonalEvidence(personalEvidenceEntity);
        memberRepository.saveAndFlush(memberEntity);
        LOG.info("Osobista Ewidencja została zapisana");
    }

}
