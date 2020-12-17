package com.shootingplace.shootingplace.services;


import com.shootingplace.shootingplace.domain.entities.ContributionEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.repositories.ContributionRepository;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public boolean addContribution(UUID memberUUID, LocalDate contributionPaymentDay) {

        List<ContributionEntity> contributionEntityList = memberRepository.findById(memberUUID)
                .orElseThrow(EntityNotFoundException::new)
                .getHistory()
                .getContributionList();

        LocalDate validThru = getDate(contributionPaymentDay);

        ContributionEntity contributionEntity = ContributionEntity.builder()
                .paymentDay(null)
                .validThru(null)
                .build();
        if (contributionEntityList.size()<1) {
            contributionEntity.setPaymentDay(contributionPaymentDay);
            contributionEntity.setValidThru(validThru.plusMonths(6));
        } else {
            contributionEntity.setPaymentDay(contributionPaymentDay);
            contributionEntity.setValidThru(contributionEntityList.get(0).getValidThru().plusMonths(6));
        }
        contributionRepository.saveAndFlush(contributionEntity);
        historyService.addContribution(memberUUID, contributionEntity);
        return true;
    }

    @NotNull
    private LocalDate getDate(LocalDate contributionPaymentDay) {
        LocalDate validThru;
        if (contributionPaymentDay.isBefore(LocalDate.of(contributionPaymentDay.getYear(), 6, 30))) {
            validThru = LocalDate.of(contributionPaymentDay.getYear(), 6, 30);
        } else {
            validThru = LocalDate.of(contributionPaymentDay.getYear(), 12, 31);
        }
        return validThru;
    }


    public boolean addContributionRecord(UUID memberUUID, LocalDate paymentDate) {
        List<ContributionEntity> contributionEntityList = memberRepository.findById(memberUUID)
                .orElseThrow(EntityNotFoundException::new)
                .getHistory()
                .getContributionList();

        LocalDate validThru = getDate(paymentDate);

        ContributionEntity contributionEntity = ContributionEntity.builder()
                .paymentDay(paymentDate)
                .validThru(validThru)
                .build();
        contributionRepository.saveAndFlush(contributionEntity);
        historyService.addContribution(memberUUID, contributionEntity);

        return true;
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
//            historyService.addContribution(memberEntity.getUuid());
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

    public boolean removeContribution(UUID memberUUID, UUID contributionUUID) {
        ContributionEntity contributionEntity = memberRepository
                .findById(memberUUID)
                .orElseThrow(EntityNotFoundException::new)
                .getHistory()
                .getContributionList()
                .stream()
                .filter(f -> f.getUuid().equals(contributionUUID))
                .collect(Collectors.toList()).get(0);


        historyService.removeContribution(memberUUID, contributionEntity);
        return true;
    }
}
