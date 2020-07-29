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
import java.util.UUID;

@Service
public class MemberPermissionsService {

    private final MemberPermissionsRepository memberPermissionsRepository;
    private final MemberRepository memberRepository;
    private final Logger LOG = LogManager.getLogger(getClass());


    public MemberPermissionsService(MemberPermissionsRepository memberPermissionsRepository, MemberRepository memberRepository) {
        this.memberPermissionsRepository = memberPermissionsRepository;
        this.memberRepository = memberRepository;
    }

    public Boolean addMemberPermissions(UUID memberUUID, MemberPermissions memberPermissions) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        if (memberEntity.getMemberPermissions() != null) {
            LOG.error("nie można już dodać Encji");
            return false;
        }
        MemberPermissionsEntity memberPermissionsEntity = Mapping.map(memberPermissions);
        memberPermissionsRepository.saveAndFlush(memberPermissionsEntity);
        memberEntity.setMemberPermissions(memberPermissionsEntity);
        memberRepository.saveAndFlush(memberEntity);
        LOG.info("Encja uprawnień została zapisana");
        return true;
    }

    public Boolean updateMemberPermissions(UUID memberUUID, MemberPermissions memberPermissions, int ordinal) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        MemberPermissionsEntity memberPermissionsEntity = memberPermissionsRepository.findById(
                memberEntity.getMemberPermissions().getUuid()).orElseThrow(EntityNotFoundException::new);
//        Instruktor
        if (memberPermissions.getInstructorNumber() != null) {
            if (!memberPermissions.getInstructorNumber().isEmpty()) {
                memberPermissionsEntity.setInstructorNumber(memberPermissions.getInstructorNumber());
            }
        }
//        Prowadzący Strzelanie
        if (memberPermissions.getShootingLeaderNumber() != null) {
            if (!memberPermissions.getShootingLeaderNumber().isEmpty()) {
                memberPermissionsEntity.setShootingLeaderNumber(memberPermissions.getShootingLeaderNumber());
            }
        }
//        Sędzia
        ArbiterClass[] arbiterClass = ArbiterClass.values();
        if (memberPermissions.getArbiterClass().ordinal() < ordinal || memberEntity.getMemberPermissions() == null) {
            memberPermissionsEntity.setArbiterClass(arbiterClass[ordinal]);
        }
        if (memberPermissions.getArbiterNumber() != null) {
            if (!memberPermissions.getArbiterNumber().isEmpty()) {
                memberPermissionsEntity.setArbiterNumber(memberPermissions.getArbiterNumber());
            }
        }
        if (memberPermissions.getArbiterPermissionValidThru() != null) {
            LocalDate date = LocalDate.of(memberPermissions.getArbiterPermissionValidThru().getYear(), 12, 31);
            memberPermissionsEntity.setArbiterPermissionValidThru(date);

        }

        return true;
    }


}
