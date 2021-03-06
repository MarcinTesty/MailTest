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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberPermissionsService {

    private final MemberPermissionsRepository memberPermissionsRepository;
    private final MemberRepository memberRepository;
    private final Logger LOG = LogManager.getLogger(getClass());


    public MemberPermissionsService(MemberPermissionsRepository memberPermissionsRepository, MemberRepository memberRepository) {
        this.memberPermissionsRepository = memberPermissionsRepository;
        this.memberRepository = memberRepository;
    }

    public Boolean updateMemberPermissions(String memberUUID, MemberPermissions memberPermissions, String ordinal) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        MemberPermissionsEntity memberPermissionsEntity = memberPermissionsRepository.findById(
                memberEntity.getMemberPermissions().getUuid()).orElseThrow(EntityNotFoundException::new);
        List<MemberEntity> collect = memberRepository.findAll()
                .stream()
                .filter(f -> !f.getErased())
                .filter(f -> f.getMemberPermissions().getInstructorNumber() != null)
                .filter(f -> f.getMemberPermissions().getShootingLeaderNumber() != null)
                .filter(f -> f.getMemberPermissions().getArbiterNumber() != null)
                .collect(Collectors.toList());
//        Instruktor
        if (memberPermissions.getInstructorNumber() != null) {
            if (!memberPermissions.getInstructorNumber().isEmpty()) {
                if (collect.stream().noneMatch(e -> e.getMemberPermissions().getInstructorNumber().equals(memberPermissions.getInstructorNumber()))) {
                    memberPermissionsEntity.setInstructorNumber(memberPermissions.getInstructorNumber());
                    LOG.info("Nadano uprawnienia Instruktora");
                } else {
                    LOG.info("Nie mo??na nada?? uprawnie??");
                    return false;
                }
            }
        }
//        Prowadz??cy Strzelanie
        if (memberPermissions.getShootingLeaderNumber() != null) {
            if (!memberPermissions.getShootingLeaderNumber().isEmpty()) {
                if (collect.stream().noneMatch(e -> e.getMemberPermissions().getShootingLeaderNumber().equals(memberPermissions.getShootingLeaderNumber()))) {
                    memberPermissionsEntity.setShootingLeaderNumber(memberPermissions.getShootingLeaderNumber());
                    LOG.info("Nadano uprawnienia Prowadz??cego Strzelanie");
                } else {
                    LOG.info("Nie mo??na nada?? uprawnie??");
                    return false;
                }

            }
        }
//        S??dzia
        if (memberPermissions.getArbiterNumber() != null) {
            if (!memberPermissions.getArbiterNumber().isEmpty()) {
                if (collect.stream().noneMatch(e -> e.getMemberPermissions().getArbiterNumber().equals(memberPermissions.getArbiterNumber()))) {
                    memberPermissionsEntity.setArbiterNumber(memberPermissions.getArbiterNumber());
                    LOG.info("Zmieniono numer s??dziego");
                }else {
                    LOG.info("Nie mo??na nada?? uprawnie??");
                    return false;
                }
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
                LOG.info("Klasa s??dziego ustawiona na pole nr " + ordinal);
            }
            if (memberPermissions.getArbiterPermissionValidThru() != null) {
                LocalDate date = LocalDate.of(memberPermissions.getArbiterPermissionValidThru().getYear(), 12, 31);
                memberPermissionsEntity.setArbiterPermissionValidThru(date);
                LOG.info("Zmieniono dat?? wa??no??ci licencji s??dziowskiej");
            }
        }
        memberPermissionsRepository.saveAndFlush(memberPermissionsEntity);

        return true;
    }


}
