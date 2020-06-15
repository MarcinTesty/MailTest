package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.LicenseEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.entities.ShootingPatentEntity;
import com.shootingplace.shootingplace.domain.models.ShootingPatent;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import com.shootingplace.shootingplace.repositories.ShootingPatentRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class ShootingPatentService {

    private final ShootingPatentRepository shootingPatentRepository;
    private final MemberRepository memberRepository;

    public ShootingPatentService(ShootingPatentRepository shootingPatentRepository,
                                 MemberRepository memberRepository) {
        this.shootingPatentRepository = shootingPatentRepository;
        this.memberRepository = memberRepository;
    }


    public boolean addPatent(UUID memberUUID, ShootingPatent shootingPatent) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        if (memberEntity.getShootingPatent() != null) {
            System.out.println("nie można już dodać patentu");
            return false;
        }
        ShootingPatentEntity shootingPatentEntity = Mapping.map(shootingPatent);
        shootingPatentRepository.saveAndFlush(shootingPatentEntity);
        memberEntity.setShootingPatent(shootingPatentEntity);
        memberRepository.saveAndFlush(memberEntity);
        System.out.println("Patent został zapisany");
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
                System.out.println("Klubowicz nie aktywny");
                return false;
            }
            if (shootingPatent.getPatentNumber() != null
                    && (memberEntity.getShootingPatent().getUuid() == shootingPatentEntity.getUuid())) {
                if (shootingPatentRepository.findByPatentNumber(shootingPatent.getPatentNumber()).isPresent()
                && !memberEntity.getShootingPatent().getPatentNumber().equals(shootingPatent.getPatentNumber())) {
                    System.out.println("ktoś już ma taki numer patentu");
                    return false;
                } else {
                    shootingPatentEntity.setPatentNumber(shootingPatent.getPatentNumber());
                    System.out.println("Wprowadzono numer patentu");

                }
            }
            if (shootingPatent.getPistolPermission() != null) {
                shootingPatentEntity.setPistolPermission(shootingPatent.getPistolPermission());
                System.out.println("Dodano dyscyplinę : Pistolet");
            }
            if (shootingPatent.getRiflePermission() != null) {
                shootingPatentEntity.setRiflePermission(shootingPatent.getRiflePermission());
                System.out.println("Dodano dyscyplinę : Karabin");
            }
            if (shootingPatent.getShotgunPermission() != null) {
                shootingPatentEntity.setShotgunPermission(shootingPatent.getShotgunPermission());
                System.out.println("Dodano dyscyplinę : Strzelba");
            }
            if (shootingPatent.getDateOfPosting() != null && shootingPatent.getDateOfPosting().isBefore(LocalDate.now())) {
                System.out.println("ustawiono datę przyznania patentu na : " + shootingPatent.getDateOfPosting());
                shootingPatentEntity.setDateOfPosting(shootingPatent.getDateOfPosting());
            }
            if (shootingPatent.getDateOfPosting() == null) {
                System.out.println("ustawiono domyślną datę nadania patentu na : " + LocalDate.now());
                shootingPatentEntity.setDateOfPosting(LocalDate.now());
            }
            shootingPatentRepository.saveAndFlush(shootingPatentEntity);
            memberEntity.setShootingPatent(shootingPatentEntity);
            memberRepository.saveAndFlush(memberEntity);
            System.out.println("Zaktualizowano patent");
            return true;
        } catch (Exception ex) {
            ex.getMessage();
            return false;
        }
    }
}