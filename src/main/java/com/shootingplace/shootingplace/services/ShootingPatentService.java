package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.HistoryEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.entities.ShootingPatentEntity;
import com.shootingplace.shootingplace.domain.models.ShootingPatent;
import com.shootingplace.shootingplace.repositories.HistoryRepository;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import com.shootingplace.shootingplace.repositories.ShootingPatentRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class ShootingPatentService {

    private final ShootingPatentRepository shootingPatentRepository;
    private final MemberRepository memberRepository;
    private final HistoryService historyService;
    private final HistoryRepository historyRepository;
    private final Logger LOG = LogManager.getLogger(getClass());


    public ShootingPatentService(ShootingPatentRepository shootingPatentRepository,
                                 MemberRepository memberRepository, HistoryService historyService, HistoryRepository historyRepository) {
        this.shootingPatentRepository = shootingPatentRepository;
        this.memberRepository = memberRepository;
        this.historyService = historyService;
        this.historyRepository = historyRepository;
    }

    public boolean updatePatent(String memberUUID, ShootingPatent shootingPatent) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        ShootingPatentEntity shootingPatentEntity = shootingPatentRepository.findById(memberEntity
                .getShootingPatent()
                .getUuid())
                .orElseThrow(EntityNotFoundException::new);
        if (shootingPatent.getPatentNumber() != null && !shootingPatent.getPatentNumber().isEmpty()) {
            if (shootingPatentRepository.findAll()
                    .stream().filter(e -> !(e.getPatentNumber() == null))
                    .anyMatch(f -> f.getPatentNumber().equals(shootingPatent.getPatentNumber()))) {
                LOG.error("ktoś już ma taki numer patentu");
                return false;
            } else {


                shootingPatentEntity.setPatentNumber(shootingPatent.getPatentNumber());
                LOG.info("Wprowadzono numer patentu");
            }
        }
        if (shootingPatent.getPistolPermission() != null) {
            if (shootingPatent.getPistolPermission().equals(true)) {
                shootingPatentEntity.setPistolPermission(shootingPatent.getPistolPermission());
                LOG.info("Dodano dyscyplinę : Pistolet");
            }
        }
        if (shootingPatent.getRiflePermission() != null) {
            if (shootingPatent.getRiflePermission().equals(true)) {
                shootingPatentEntity.setRiflePermission(shootingPatent.getRiflePermission());
                LOG.info("Dodano dyscyplinę : Karabin");
            }
        }
        if (shootingPatent.getShotgunPermission() != null) {
            if (shootingPatent.getShotgunPermission().equals(true)) {
                shootingPatentEntity.setShotgunPermission(shootingPatent.getShotgunPermission());
                LOG.info("Dodano dyscyplinę : Strzelba");
            }
        }
        if (shootingPatent.getDateOfPosting() != null) {
            LOG.info("ustawiono datę przyznania patentu na : " + shootingPatent.getDateOfPosting());
            shootingPatentEntity.setDateOfPosting(shootingPatent.getDateOfPosting());

        }
        shootingPatentRepository.saveAndFlush(shootingPatentEntity);
        LOG.info("Zaktualizowano patent");
        HistoryEntity historyEntity =
                memberEntity.getHistory();
        if (shootingPatentEntity.getDateOfPosting() != null) {
            if (shootingPatentEntity.getPistolPermission()) {
                historyService.addDateToPatentPermissions(memberUUID, shootingPatent.getDateOfPosting(), 0);
            }
            if (shootingPatentEntity.getRiflePermission()) {
                historyService.addDateToPatentPermissions(memberUUID, shootingPatent.getDateOfPosting(), 1);
            }
            if (shootingPatentEntity.getShotgunPermission()) {
                historyService.addDateToPatentPermissions(memberUUID, shootingPatent.getDateOfPosting(), 2);
            }
            if (shootingPatentEntity.getDateOfPosting() != null) {
                historyEntity.setPatentFirstRecord(true);
            }
            historyRepository.saveAndFlush(historyEntity);
        }
        return true;
    }
}