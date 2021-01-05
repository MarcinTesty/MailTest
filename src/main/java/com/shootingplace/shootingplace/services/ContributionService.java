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
    private final Logger LOG = LogManager.getLogger(getClass());


    public ContributionService(ContributionRepository contributionRepository, MemberRepository memberRepository, HistoryService historyService) {
        this.contributionRepository = contributionRepository;
        this.memberRepository = memberRepository;
        this.historyService = historyService;
    }

    public boolean addContribution(UUID memberUUID, LocalDate contributionPaymentDay) {

        List<ContributionEntity> contributionEntityList = memberRepository.findById(memberUUID)
                .orElseThrow(EntityNotFoundException::new)
                .getHistory()
                .getContributionList();
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);

        LocalDate validThru = getDate(contributionPaymentDay);

        ContributionEntity contributionEntity = ContributionEntity.builder()
                .paymentDay(null)
                .validThru(null)
                .build();

        if (memberEntity.getAdult()) {
            if (contributionEntityList.size() < 1) {
                contributionEntity.setPaymentDay(contributionPaymentDay);
                contributionEntity.setValidThru(validThru.plusMonths(6));
            } else {
                contributionEntity.setPaymentDay(contributionPaymentDay);
                if (contributionEntityList.get(0).getValidThru().equals(LocalDate.of(validThru.getYear(), 6, 30))) {
                    contributionEntity.setValidThru(LocalDate.of(contributionEntityList.get(0).getValidThru().getYear(), 12, 31));
                } else {
                    contributionEntity.setValidThru(contributionEntityList.get(0).getValidThru().plusMonths(6));
                }
            }
            contributionRepository.saveAndFlush(contributionEntity);
            historyService.addContribution(memberUUID, contributionEntity);
            return true;
        }
        if (!memberEntity.getAdult()) {
            if (contributionEntityList.size() < 1) {
                contributionEntity.setPaymentDay(contributionPaymentDay);
                contributionEntity.setValidThru(validThru.plusMonths(6));
            } else {
                contributionEntity.setPaymentDay(contributionPaymentDay);
                if (contributionEntityList.get(0).getValidThru().equals(LocalDate.of(validThru.getYear(), 2, 28))) {
                    contributionEntity.setValidThru(LocalDate.of(contributionEntity.getValidThru().getYear(), 8, 31));
                }
                contributionEntity.setValidThru(contributionEntityList.get(0).getValidThru().plusMonths(6));
            }
            contributionRepository.saveAndFlush(contributionEntity);
            historyService.addContribution(memberUUID, contributionEntity);
            return true;
        }
        return false;
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

    private ContributionEntity getContributionEntity(UUID memberUUID, LocalDate contributionPaymentDay) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);

        LocalDate validThru = contributionPaymentDay;

        if (memberEntity.getAdult()) {

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
        }
        if (!memberEntity.getAdult()) {
            if (validThru.isBefore(LocalDate.of(contributionPaymentDay.getYear(), 2, 28))) {
                validThru = LocalDate.of(contributionPaymentDay.getYear(), 2, 28);
            } else {
                validThru = LocalDate.of(contributionPaymentDay.getYear(), 8, 31);
            }
            if (memberEntity.getHistory().getContributionList() != null) {
                if (!memberEntity.getHistory().getContributionList().isEmpty()) {
                    validThru = memberEntity.getHistory().getContributionList().get(0).getValidThru();
                }
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
