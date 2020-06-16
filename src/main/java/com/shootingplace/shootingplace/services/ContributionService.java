package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.ContributionEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.models.Contribution;
import com.shootingplace.shootingplace.repositories.ContributionRepository;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class ContributionService {

    private final ContributionRepository contributionRepository;
    private final MemberRepository memberRepository;

    public ContributionService(ContributionRepository contributionRepository, MemberRepository memberRepository) {
        this.contributionRepository = contributionRepository;
        this.memberRepository = memberRepository;
    }

    public boolean addContribution(UUID memberUUID, Contribution contribution) {
        MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
        if (memberEntity.getContribution() != null) {
            System.out.println("nie można już dodać pola ze składką");
            return false;
        }
        ContributionEntity contributionEntity = Mapping.map(contribution);
        contributionRepository.saveAndFlush(contributionEntity);
        memberEntity.setContribution(contributionEntity);
        memberRepository.saveAndFlush(memberEntity);
        System.out.println("Składka została zapisana");
        return true;
    }

    public boolean updateContribution(UUID memberUUID, Contribution contribution) {
        try {
            MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
            ContributionEntity contributionEntity = contributionRepository.findById(memberEntity
                    .getContribution()
                    .getUuid())
                    .orElseThrow(EntityNotFoundException::new);
            if (contribution.getContribution() != null) {
                LocalDate localDate = contribution.getContribution();
                if (localDate.isBefore(LocalDate.of(contribution.getContribution().getYear(), 6, 30))) {
                    localDate = LocalDate.of(contribution.getContribution().getYear(), 6, 30);
                } else {
                    localDate = LocalDate.of(contribution.getContribution().getYear(), 12, 31);
                }
                contributionEntity.setContribution(localDate);
                System.out.println("Składka przedłużona do : " + contributionEntity.getContribution());
            }
            contributionRepository.saveAndFlush(contributionEntity);
            memberRepository.saveAndFlush(memberEntity);
            System.out.println("zaktualizowano składkę");
            return true;
        } catch (Exception ex) {
            ex.getMessage();
            return false;
        }
    }

    public boolean prolongContribution(UUID memberUUID, Integer contributionAmount) {
        if (memberRepository.existsById(memberUUID)) {
            MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);
            if (memberEntity.getActive().equals(false)) {
                System.out.println("Klubowicz nie aktywny");
                return false;
            } else {
                LocalDate prolong = memberEntity.getContribution().getContribution().plusMonths(6 * contributionAmount);
                ContributionEntity contributionEntity = contributionRepository.findById(memberEntity
                        .getContribution()
                        .getUuid())
                        .orElseThrow(EntityNotFoundException::new);
                contributionEntity.setContribution(prolong);
                contributionRepository.saveAndFlush(contributionEntity);
                memberEntity.setContribution(contributionEntity);
                memberRepository.saveAndFlush(memberEntity);
                System.out.println("Składka zostałą przedłużona do : " + contributionEntity.getContribution());
            }
            return true;
        } else
            System.out.println("Nie znaleziono takiego klubowicza");
        return false;
    }
}

