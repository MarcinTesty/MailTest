package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.CaliberEntity;
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
import java.util.Arrays;
import java.util.List;
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

    public void collectAmmoData(UUID memberUUID) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        List<CaliberEntity> caliberEntities = caliberRepository.findAll();
        PersonalEvidenceEntity personalEvidence = memberEntity.getPersonalEvidence();
        String[] evidenceAmmo = new String[caliberEntities.size()];
        Integer ammo = 0;
        for (int i = 0; i < caliberEntities.size(); i++) {
            List<MemberEntity> members = caliberEntities.get(i).getMembers();
            Integer[] integers = caliberEntities.get(i).getAmmoUsed();
            for (int j = 0; j < members.size(); j++) {
                if (members.get(j).equals(memberEntity)) {
                    ammo = ammo + integers[j];
                    integers[j] = ammo;
                }
            }
            evidenceAmmo[i] = String.valueOf(ammo);
            evidenceAmmo[i] = evidenceAmmo[i] +" szt. "+ caliberEntities.get(i).getName();
            ammo = 0;

        }
        System.out.println(Arrays.toString(evidenceAmmo));
        personalEvidence.setAmmo(evidenceAmmo);
        personalEvidenceRepository.saveAndFlush(personalEvidence);
    }

    void addPersonalEvidence(UUID memberUUID, PersonalEvidence personalEvidence) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        if (memberEntity.getPersonalEvidence() != null) {
            LOG.error("nie można już dodać pola");
        }
        PersonalEvidenceEntity personalEvidenceEntity = Mapping.map(personalEvidence);
        personalEvidenceRepository.saveAndFlush(personalEvidenceEntity);
        memberEntity.setPersonalEvidence(personalEvidenceEntity);
        memberRepository.saveAndFlush(memberEntity);
        LOG.info("Osobista Ewidencja została zapisana");
    }
}
