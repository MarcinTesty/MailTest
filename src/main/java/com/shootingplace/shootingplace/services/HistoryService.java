package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.HistoryEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.models.History;
import com.shootingplace.shootingplace.repositories.ContributionRepository;
import com.shootingplace.shootingplace.repositories.HistoryRepository;
import com.shootingplace.shootingplace.repositories.LicenseRepository;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final ContributionRepository contributionRepository;
    private final LicenseRepository licenseRepository;
    private final MemberRepository memberRepository;
    private final Logger LOG = LogManager.getLogger(getClass());


    public HistoryService(HistoryRepository historyRepository, ContributionRepository contributionRepository, LicenseRepository licenseRepository, MemberRepository memberRepository) {
        this.historyRepository = historyRepository;
        this.contributionRepository = contributionRepository;
        this.licenseRepository = licenseRepository;
        this.memberRepository = memberRepository;
    }

    void createHistory(UUID memberUUID, History history) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID)
                .orElseThrow(EntityNotFoundException::new);
        HistoryEntity historyEntity = Mapping.map(history);
        historyEntity.setContributionRecord(new LocalDate[]{LocalDate.now()});
        historyEntity.setLicenseHistory(new String[3]);
        historyEntity.setPatentDay(new LocalDate[3]);
        historyRepository.saveAndFlush(historyEntity);
        memberEntity.setHistory(historyEntity);
        memberRepository.saveAndFlush(memberEntity);
        LOG.info("Historia zaostała utworzona");
    }

    void addContributionRecord(UUID memberUUID) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        HistoryEntity historyEntity = historyRepository.findById(memberEntity.getHistory().getUuid())
                .orElseThrow(EntityNotFoundException::new);
        LocalDate[] newState = new LocalDate[historyEntity.getContributionRecord().length + 1];

        for (int i = 0; i <= historyEntity.getContributionRecord().length - 1; i++) {
            newState[i] = historyEntity.getContributionRecord()[i];
            newState[i + 1] = LocalDate.now();
        }

        historyEntity.setContributionRecord(newState);
        historyRepository.saveAndFlush(historyEntity);
    }

    void addLicenseHistoryRecord(UUID memberUUID, int index) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        HistoryEntity historyEntity = historyRepository.findById(memberEntity.getHistory().getUuid())
                .orElseThrow(EntityNotFoundException::new);
        String[] licenseTab = historyEntity.getLicenseHistory().clone();
        if (index == 0) {
            licenseTab[0] = "Pistolet";
        }
        if (index == 1) {
            licenseTab[1] = "Karabin";
        }
        if (index == 2) {
            licenseTab[2] = "Strzelba";
        }
        historyEntity.setLicenseHistory(licenseTab);
        historyRepository.saveAndFlush(historyEntity);
    }

    void addDateToPatentPermissions(UUID memberUUID, int index) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        HistoryEntity historyEntity = historyRepository.findById(memberEntity.getHistory().getUuid())
                .orElseThrow(EntityNotFoundException::new);
        LocalDate[] dateTab = historyEntity.getPatentDay().clone();
        if (index == 0) {
            if (memberEntity.getShootingPatent().getDateOfPosting() != null) {
                if (!memberEntity.getHistory().getPatentFirstRecord()) {
                    dateTab[0] = memberEntity.getShootingPatent().getDateOfPosting();
                    LOG.info("Pobrano datę patentu dla Pistoletu");
                }
                if (memberEntity.getHistory().getPatentFirstRecord() && historyEntity.getPatentDay()[0] == null) {
                    dateTab[0] = LocalDate.now();
                    LOG.info("Ustawiono datę patentu Karabinu na domyślną");
                }
            }
        }
        if (index == 1) {
            if (memberEntity.getShootingPatent().getDateOfPosting() != null) {
                if (!memberEntity.getHistory().getPatentFirstRecord()) {
                    dateTab[1] = memberEntity.getShootingPatent().getDateOfPosting();
                    LOG.info("Pobrano datę patentu dla Karabinu");
                }
                if (memberEntity.getHistory().getPatentFirstRecord() && historyEntity.getPatentDay()[1] == null) {
                    dateTab[1] = LocalDate.now();
                    LOG.info("Ustawiono datę patentu Karabinu na domyślną");
                }
            }
        }
        if (index == 2) {
            if (memberEntity.getShootingPatent().getDateOfPosting() != null) {
                if (!memberEntity.getHistory().getPatentFirstRecord()) {
                    dateTab[2] = memberEntity.getShootingPatent().getDateOfPosting();
                    LOG.info("Pobrano datę patentu dla Strzelby");
                }
                if (memberEntity.getHistory().getPatentFirstRecord() && historyEntity.getPatentDay()[2] == null) {
                    dateTab[2] = LocalDate.now();
                    LOG.info("Ustawiono datę patentu Strzelby na domyślną");
                }
            }
        }
        if (!historyEntity.getPatentFirstRecord()) {
            LOG.info("Już wpisano datę pierwszego nadania patentu");
        }
        historyEntity.setPatentDay(dateTab);
        historyRepository.saveAndFlush(historyEntity);

    }

}
