package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.entities.MemberPermissionsEntity;
import com.shootingplace.shootingplace.domain.enums.ArbiterClass;
import com.shootingplace.shootingplace.domain.models.MemberPermissions;
import com.shootingplace.shootingplace.repositories.MemberPermissionsRepository;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;

@Service
public class MemberPermissionsService {

    private final MemberPermissionsRepository memberPermissionsRepository;
    private final MemberRepository memberRepository;
    private final Logger LOG = LogManager.getLogger(getClass());


    public MemberPermissionsService(MemberPermissionsRepository memberPermissionsRepository, MemberRepository memberRepository) {
        this.memberPermissionsRepository = memberPermissionsRepository;
        this.memberRepository = memberRepository;
    }

    void addMemberPermissions(String memberUUID, MemberPermissions memberPermissions) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        if (memberEntity.getMemberPermissions() != null) {
            LOG.error("nie można już dodać Encji");
            return;
        }
        MemberPermissionsEntity memberPermissionsEntity = Mapping.map(memberPermissions);
        memberPermissionsRepository.saveAndFlush(memberPermissionsEntity);
        memberEntity.setMemberPermissions(memberPermissionsEntity);
        memberRepository.saveAndFlush(memberEntity);
        LOG.info("Encja uprawnień została zapisana");
    }

    public Boolean updateMemberPermissions(String memberUUID, MemberPermissions memberPermissions, String ordinal) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        MemberPermissionsEntity memberPermissionsEntity = memberPermissionsRepository.findById(
                memberEntity.getMemberPermissions().getUuid()).orElseThrow(EntityNotFoundException::new);
//        Instruktor
        if (memberPermissions.getInstructorNumber() != null) {
            if (!memberPermissions.getInstructorNumber().isEmpty()) {
                memberPermissionsEntity.setInstructorNumber(memberPermissions.getInstructorNumber());

                LOG.info("Uprawnienia Instruktora");
            }
        }
//        Prowadzący Strzelanie
        if (memberPermissions.getShootingLeaderNumber() != null) {
            if (!memberPermissions.getShootingLeaderNumber().isEmpty()) {
                memberPermissionsEntity.setShootingLeaderNumber(memberPermissions.getShootingLeaderNumber());

                LOG.info("Uprawnienia Prowadzącego Strzelanie");
            }
        }
//        Sędzia
        if (memberPermissions.getArbiterNumber() != null) {
            if (!memberPermissions.getArbiterNumber().isEmpty()) {
                memberPermissionsEntity.setArbiterNumber(memberPermissions.getArbiterNumber());
                LOG.info("Zmieniono numer sędziego");
            }
            if (ordinal != null && !ordinal.isEmpty()) {
                if (ordinal.equals("1")) {
                    memberPermissionsEntity.setArbiterClass(ArbiterClass.CLASS_3.getName());
                }
                if (ordinal.equals("2")) {
                    memberPermissionsEntity.setArbiterClass(ArbiterClass.CLASS_2.getName());
                }
                if (ordinal.equals("3")) {
                    memberPermissionsEntity.setArbiterClass(ArbiterClass.CLASS_1.getName());
                }
                if (ordinal.equals("4")) {
                    memberPermissionsEntity.setArbiterClass(ArbiterClass.CLASS_STATE.getName());
                }
                if (ordinal.equals("5")) {
                    memberPermissionsEntity.setArbiterClass(ArbiterClass.CLASS_INTERNATIONAL.getName());
                }
                LOG.info("Klasa sędziego ustawiona na pole nr "+ ordinal);
            }
            if (memberPermissions.getArbiterPermissionValidThru() != null) {
                LocalDate date = LocalDate.of(memberPermissions.getArbiterPermissionValidThru().getYear(), 12, 31);
                memberPermissionsEntity.setArbiterPermissionValidThru(date);
            }
            LOG.info("Zmieniono datę ważności licencji sędziowskiej");
        }
        memberPermissionsRepository.saveAndFlush(memberPermissionsEntity);

        return true;
    }


}
