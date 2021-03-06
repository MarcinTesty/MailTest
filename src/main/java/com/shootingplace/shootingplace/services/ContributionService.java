package com.shootingplace.shootingplace.services;


import com.shootingplace.shootingplace.domain.entities.ContributionEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.models.MemberDTO;
import com.shootingplace.shootingplace.repositories.ContributionRepository;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContributionService {

    private final ContributionRepository contributionRepository;
    private final MemberRepository memberRepository;
    private final HistoryService historyService;
    private final ChangeHistoryService changeHistoryService;
    private final Logger LOG = LogManager.getLogger(getClass());


    public ContributionService(ContributionRepository contributionRepository, MemberRepository memberRepository, HistoryService historyService, ChangeHistoryService changeHistoryService) {
        this.contributionRepository = contributionRepository;
        this.memberRepository = memberRepository;
        this.historyService = historyService;
        this.changeHistoryService = changeHistoryService;
    }

    public boolean addContribution(String memberUUID, LocalDate contributionPaymentDay, String pinCode) {

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
                if (contributionPaymentDay.isBefore(LocalDate.of(validThru.getYear(), 6, 30))) {
                    contributionEntity.setValidThru(LocalDate.of(validThru.getYear(), 6, 30));
                } else {
                    contributionEntity.setValidThru(LocalDate.of(validThru.getYear(), 12, 31));
                }
            } else {
                contributionEntity.setPaymentDay(contributionPaymentDay);
                if (contributionEntityList.get(0).getValidThru().equals(LocalDate.of(validThru.getYear(), 6, 30))) {
                    contributionEntity.setValidThru(LocalDate.of(contributionEntityList.get(0).getValidThru().getYear(), 12, 31));
                } else {
                    contributionEntity.setValidThru(contributionEntityList.get(0).getValidThru().plusMonths(6));
                }
            }
        }
        if (!memberEntity.getAdult()) {
            if (contributionEntityList.size() < 1) {
                contributionEntity.setPaymentDay(contributionPaymentDay);
                if (contributionPaymentDay.isBefore(LocalDate.of(validThru.getYear(), 2, 28))) {
                    contributionEntity.setValidThru(LocalDate.of(contributionEntity.getValidThru().getYear(), 2, 28));
                } else {
                    contributionEntity.setValidThru(LocalDate.of(contributionEntity.getValidThru().getYear(), 8, 31));
                }
                contributionEntity.setValidThru(validThru.plusMonths(6));
            } else {
                contributionEntity.setPaymentDay(contributionPaymentDay);
                if (contributionEntityList.get(0).getValidThru().equals(LocalDate.of(validThru.getYear(), 2, 28))) {
                    contributionEntity.setValidThru(LocalDate.of(contributionEntity.getValidThru().getYear(), 8, 31));
                }
                contributionEntity.setValidThru(contributionEntityList.get(0).getValidThru().plusMonths(6));
            }
        }
        contributionRepository.saveAndFlush(contributionEntity);
        historyService.addContribution(memberUUID, contributionEntity);
        changeHistoryService.addRecordToChangeHistory(pinCode, contributionEntity.getClass().getSimpleName() + " addContribution", memberUUID);
        memberEntity.setActive(!memberEntity.getHistory().getContributionList().get(0).getValidThru().plusMonths(3).isBefore(LocalDate.now()));
        LOG.info("zmieniono " + memberEntity.getSecondName());
        memberRepository.saveAndFlush(memberEntity);
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


    public boolean addContributionRecord(String memberUUID, LocalDate paymentDate, String pinCode) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);

        LocalDate validThru = getDate(paymentDate);

        ContributionEntity contributionEntity = ContributionEntity.builder()
                .paymentDay(paymentDate)
                .validThru(validThru)
                .build();
        contributionRepository.saveAndFlush(contributionEntity);
        historyService.addContribution(memberUUID, contributionEntity);
        changeHistoryService.addRecordToChangeHistory(pinCode, contributionEntity.getClass().getSimpleName() + " addContributionRecord", memberUUID);
        memberEntity.setActive(!memberEntity.getHistory().getContributionList().get(0).getValidThru().plusMonths(3).isBefore(LocalDate.now()));
        LOG.info("zmieniono " + memberEntity.getSecondName());
        memberRepository.saveAndFlush(memberEntity);
        return true;
    }


    ContributionEntity addFirstContribution(String memberUUID, LocalDate contributionPaymentDay) {

        ContributionEntity contributionEntity = getContributionEntity(memberUUID, contributionPaymentDay);
        LOG.info("utworzono pierwszą składkę");
        return contributionRepository.saveAndFlush(contributionEntity);
    }

    private ContributionEntity getContributionEntity(String memberUUID, LocalDate contributionPaymentDay) {
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

    public boolean removeContribution(String memberUUID, String contributionUUID, String pinCode) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);

        ContributionEntity contributionEntity = memberRepository
                .findById(memberUUID)
                .orElseThrow(EntityNotFoundException::new)
                .getHistory()
                .getContributionList()
                .stream()
                .filter(f -> f.getUuid().equals(contributionUUID))
                .collect(Collectors.toList()).get(0);


        historyService.removeContribution(memberUUID, contributionEntity);
        contributionRepository.delete(contributionEntity);
        changeHistoryService.addRecordToChangeHistory(pinCode, contributionEntity.getClass().getSimpleName() + " removeContributiond", memberUUID);
        memberEntity.setActive(!memberEntity.getHistory().getContributionList().get(0).getValidThru().plusMonths(3).isBefore(LocalDate.now()));
        LOG.info("zmieniono " + memberEntity.getSecondName());
        memberRepository.saveAndFlush(memberEntity);

        return true;

    }

    public List<MemberDTO> getContributionSum(LocalDate firstDate, LocalDate secondDate, boolean condition) {

        List<MemberEntity> memberEntities = memberRepository.findAll();

        memberEntities.forEach(t -> t.getHistory().getContributionList().forEach(g -> {
            if (g.getHistoryUUID() == null) {
                g.setHistoryUUID(t.getUuid());
                contributionRepository.saveAndFlush(g);
            }
        }));

        contributionRepository.findAll().forEach(e -> {
            if (e.getHistoryUUID() == null) {
                contributionRepository.delete(e);
            }
        });

        List<MemberDTO> collect1 = new ArrayList<>();

        memberRepository.findAll().stream().filter(f -> f.getAdult().equals(condition))
                .forEach(e -> e.getHistory().getContributionList()
                        .stream()
                        .filter(f -> f.getHistoryUUID() != null)
                        .filter(f -> f.getPaymentDay().isAfter(firstDate.minusDays(1)))
                        .filter(f -> f.getPaymentDay().isBefore(secondDate.plusDays(1)))
                        .forEach(d -> collect1.add(Mapping.map2(e))));

        collect1.sort(Comparator.comparing(MemberDTO::getSecondName).thenComparing(MemberDTO::getFirstName));

        return collect1;

    }

    public List<MemberDTO> getJoinDateSum(LocalDate firstDate, LocalDate secondDate) {

        return memberRepository.findAll().stream()
                .filter(f -> f.getJoinDate().isAfter(firstDate.minusDays(1)))
                .filter(f -> f.getJoinDate().isBefore(secondDate.plusDays(1)))
                .map(Mapping::map2)
                .sorted(Comparator.comparing(MemberDTO::getSecondName).thenComparing(MemberDTO::getFirstName))
                .collect(Collectors.toList());
    }

    public List<MemberDTO> getErasedMembersSum(LocalDate firstDate, LocalDate secondDate) {

        return memberRepository.findAll().stream()
                .filter(f -> f.getErasedEntity() != null)
                .filter(f -> f.getErasedEntity().getDate().isAfter(firstDate.minusDays(1)))
                .filter(f -> f.getErasedEntity().getDate().isBefore(secondDate.plusDays(1)))
                .map(Mapping::map2)
                .sorted(Comparator.comparing(MemberDTO::getSecondName).thenComparing(MemberDTO::getFirstName))
                .collect(Collectors.toList());

    }
}
