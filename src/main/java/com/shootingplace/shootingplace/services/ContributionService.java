package com.shootingplace.shootingplace.services;


import com.shootingplace.shootingplace.domain.entities.ContributionEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.repositories.ContributionRepository;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class ContributionService {

    private final ContributionRepository contributionRepository;
    private final MemberRepository memberRepository;
    private final HistoryService historyService;
    private final FilesService filesService;
    private final Logger LOG = LogManager.getLogger(getClass());


    public ContributionService(ContributionRepository contributionRepository, MemberRepository memberRepository, HistoryService historyService, FilesService filesService) {
        this.contributionRepository = contributionRepository;
        this.memberRepository = memberRepository;
        this.historyService = historyService;
        this.filesService = filesService;
    }

//    void addContribution(UUID memberUUID, Contribution contribution) {
//        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
//        if (memberEntity.getContribution() != null) {
//            LOG.error("nie można już dodać pola ze składką");
//        }
//        ContributionEntity contributionEntity = Mapping.map(contribution);
//        contributionRepository.saveAndFlush(contributionEntity);
//        memberEntity.setContribution(contributionEntity);
//        memberRepository.saveAndFlush(memberEntity);
//        LOG.info("Składka została zapisana");
//    }

    void addContribution(UUID memberUUID, LocalDate contributionPaymentDay) {

        ContributionEntity contributionEntity = getContributionEntity(memberUUID, contributionPaymentDay);

        contributionRepository.saveAndFlush(contributionEntity);
        historyService.addContributionRecord(memberUUID, contributionEntity);
    }


    ContributionEntity addFirstContribution(UUID memberUUID, LocalDate contributionPaymentDay) {

        ContributionEntity contributionEntity = getContributionEntity(memberUUID, contributionPaymentDay);
        LOG.info("utworzono pierwszą składkę");
        return contributionRepository.saveAndFlush(contributionEntity);
    }

//    public void updateContribution(UUID memberUUID, Contribution contribution) {
//        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
//        ContributionEntity contributionEntity = contributionRepository.findById(memberEntity
//                .getContribution()
//                .getUuid())
//                .orElseThrow(EntityNotFoundException::new);
//        if (contribution.getContribution() != null) {
//            LocalDate localDate = contribution.getContribution();
//            if (localDate.isBefore(LocalDate.of(contribution.getContribution().getYear(), 6, 30))) {
//                localDate = LocalDate.of(contribution.getContribution().getYear(), 6, 30);
//            } else {
//                localDate = LocalDate.of(contribution.getContribution().getYear(), 12, 31);
//            }
//            contributionEntity.setPaymentDay(LocalDate.now());
//            contributionEntity.setContribution(localDate);
//            LOG.info("Składka przedłużona do : " + contributionEntity.getContribution());
//            LOG.info("składka opłacona dnia : " + contributionEntity.getPaymentDay());
//        }
//        contributionEntity.setPaymentDay(LocalDate.now());
//        contributionRepository.saveAndFlush(contributionEntity);
//        memberRepository.saveAndFlush(memberEntity);
//        LOG.info("zaktualizowano składkę");
//
//    }

    //    public boolean prolongContribution(UUID memberUUID) {
//
//        if (memberRepository.existsById(memberUUID)) {
//            MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
//
//            LocalDate prolong = memberEntity.getContribution().getContribution().plusMonths(6);
//            if (prolong.getMonth().getValue() == 12) {
//                prolong = prolong.plusDays(1);
//            }
//            if (memberEntity.getContribution().getContribution().isAfter(LocalDate.of(LocalDate.now().getYear(), 9, 30)) |
//                    memberEntity.getContribution().getContribution().isAfter(LocalDate.of(LocalDate.now().getYear(), 3, 31))) {
//                if (!memberEntity.getActive()) {
//                    LOG.info("klubowicz jest już aktywny");
//                    memberEntity.toggleActive();
//                }
//            } else {
//                memberEntity.toggleActive();
//            }
//            ContributionEntity contributionEntity = contributionRepository.findById(memberEntity
//                    .getContribution()
//                    .getUuid())
//                    .orElseThrow(EntityNotFoundException::new);
//            contributionEntity.setContribution(prolong);
//            contributionEntity.setPaymentDay(LocalDate.now());
//
//            historyService.addContributionRecord(memberEntity.getUuid());
//            contributionRepository.saveAndFlush(contributionEntity);
//            memberEntity.setContribution(contributionEntity);
//            memberRepository.saveAndFlush(memberEntity);
//            try {
//                filesService.contributionConfirm(memberUUID);
//            } catch (DocumentException | IOException e) {
//                e.printStackTrace();
//            }
//            LOG.info("Składka została przedłużona do : " + contributionEntity.getContribution());
//            LOG.info("Składka została przedłużona dnia : " + contributionEntity.getPaymentDay());
//            return true;
//        } else
//            LOG.error("Nie znaleziono takiego klubowicza");
//        return false;
//    }
    private ContributionEntity getContributionEntity(UUID memberUUID, LocalDate contributionPaymentDay) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);

        LocalDate validThru = contributionPaymentDay;

        if (validThru.isBefore(LocalDate.of(contributionPaymentDay.getYear(), 6, 30))) {
            validThru = LocalDate.of(contributionPaymentDay.getYear(), 6, 30);
        } else {
            validThru = LocalDate.of(contributionPaymentDay.getYear(), 12, 31);
        }
        if (memberEntity.getHistory().getContributionList() != null) {
            if (!memberEntity.getHistory().getContributionList().isEmpty()) {
                validThru = memberEntity.getHistory().getContributionList().get(0).getValidThru();
            }
        }
        return ContributionEntity.builder()
                .paymentDay(contributionPaymentDay)
                .validThru(validThru)
                .build();
    }
}
