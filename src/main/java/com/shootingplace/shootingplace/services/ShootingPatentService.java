package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.entities.ShootingPatentEntity;
import com.shootingplace.shootingplace.domain.models.ShootingPatent;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import com.shootingplace.shootingplace.repositories.ShootingPatentRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;


import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class ShootingPatentService {

    private final ShootingPatentRepository shootingPatentRepository;
    private final MemberRepository memberRepository;
    private final Logger LOG = LogManager.getLogger(getClass());


    public ShootingPatentService(ShootingPatentRepository shootingPatentRepository,
                                 MemberRepository memberRepository) {
        this.shootingPatentRepository = shootingPatentRepository;
        this.memberRepository = memberRepository;
    }


    public boolean addPatent(UUID memberUUID, ShootingPatent shootingPatent) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        if (memberEntity.getShootingPatent() != null) {
            LOG.error("nie można już dodać patentu");
            return false;
        }
        ShootingPatentEntity shootingPatentEntity = Mapping.map(shootingPatent);
        shootingPatentRepository.saveAndFlush(shootingPatentEntity);
        memberEntity.setShootingPatent(shootingPatentEntity);
        memberRepository.saveAndFlush(memberEntity);
        LOG.info("Patent został zapisany");
        return true;
    }

    public boolean updatePatent(UUID memberUUID, ShootingPatent shootingPatent) {
        try {
            MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
            ShootingPatentEntity shootingPatentEntity = shootingPatentRepository.findById(memberEntity
                    .getShootingPatent()
                    .getUuid())
                    .orElseThrow(EntityNotFoundException::new);
            if (memberEntity.getActive().equals(false)) {
                LOG.warn("Klubowicz nie aktywny");
                return false;
            }
            if (shootingPatent.getPatentNumber().trim().isEmpty()) {
                shootingPatent.setPatentNumber(shootingPatentEntity.getPatentNumber());
            }
            if (shootingPatent.getPatentNumber() != null
                    && (memberEntity.getShootingPatent().getUuid() == shootingPatentEntity.getUuid())) {
                if (shootingPatentRepository.findByPatentNumber(shootingPatent.getPatentNumber()).isPresent()
                        && !memberEntity.getShootingPatent().getPatentNumber().equals(shootingPatent.getPatentNumber())) {
                    LOG.error("ktoś już ma taki numer patentu");
                    return false;
                } else {
                    shootingPatentEntity.setPatentNumber(shootingPatent.getPatentNumber());
                    LOG.info("Wprowadzono numer patentu");

                }
            }
            if (shootingPatent.getPistolPermission()) {
                shootingPatentEntity.setPistolPermission(shootingPatent.getPistolPermission());
                LOG.info("Dodano dyscyplinę : Pistolet");
            }
            if (shootingPatent.getRiflePermission()){
                shootingPatentEntity.setRiflePermission(shootingPatent.getRiflePermission());
                LOG.info("Dodano dyscyplinę : Karabin");
            }
            if (shootingPatent.getShotgunPermission()) {
                shootingPatentEntity.setShotgunPermission(shootingPatent.getShotgunPermission());
                LOG.info("Dodano dyscyplinę : Strzelba");
            }
            if (shootingPatent.getDateOfPosting() != null && shootingPatent.getDateOfPosting().isBefore(LocalDate.now())) {
                System.out.println("ustawiono datę przyznania patentu na : " + shootingPatent.getDateOfPosting());
                shootingPatentEntity.setDateOfPosting(shootingPatent.getDateOfPosting());
            }
            if (shootingPatent.getDateOfPosting() == null) {
                LOG.info("ustawiono domyślną datę nadania patentu na : " + LocalDate.now());
                shootingPatentEntity.setDateOfPosting(LocalDate.now());
            }
            shootingPatentRepository.saveAndFlush(shootingPatentEntity);
            memberEntity.setShootingPatent(shootingPatentEntity);
            memberRepository.saveAndFlush(memberEntity);
            LOG.info("Zaktualizowano patent");
            return true;
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
            LOG.error("ktoś już ma taki numer patentu");
            return false;
        }
    }
}