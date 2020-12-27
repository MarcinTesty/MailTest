package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.CompetitionMembersListEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.repositories.CompetitionMembersListRepository;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

@Service
public class CompetitionMembersListService {

    private final MemberRepository memberRepository;
    private final CompetitionMembersListRepository competitionMembersListRepository;
    private final HistoryService historyService;
    private final Logger LOG = LogManager.getLogger();


    public CompetitionMembersListService(MemberRepository memberRepository, CompetitionMembersListRepository competitionMembersListRepository, HistoryService historyService) {
        this.memberRepository = memberRepository;
        this.competitionMembersListRepository = competitionMembersListRepository;
        this.historyService = historyService;
    }

    public boolean addMemberToCompetitionList(UUID competitionUUID, int legitimationNumber) {
        CompetitionMembersListEntity list = competitionMembersListRepository.findById(competitionUUID).orElseThrow(EntityNotFoundException::new);
        MemberEntity member = memberRepository.findAll().stream().filter(f->f.getLegitimationNumber().equals(legitimationNumber)).findFirst().orElseThrow(EntityNotFoundException::new);
        List<MemberEntity> membersList = list.getMembersList();
        if (!membersList.contains(member)) {
            membersList.add(member);
            competitionMembersListRepository.saveAndFlush(list);
            LOG.info("Dodano Klubowicza do Listy");
            historyService.addCompetitionRecord(member.getUuid(),list);
            return true;
        } else {
            LOG.info("Nie można dodać bo klubowicz już się znajduje na liście");
            return false;
        }

    }


    public boolean removeMemberFromList(UUID competitionUUID,  int legitimationNumber) {
        CompetitionMembersListEntity list = competitionMembersListRepository.findById(competitionUUID).orElseThrow(EntityNotFoundException::new);
        MemberEntity member = memberRepository.findAll().stream().filter(f->f.getLegitimationNumber().equals(legitimationNumber)).findFirst().orElseThrow(EntityNotFoundException::new);
        List<MemberEntity> membersList = list.getMembersList();

        if (membersList.contains(member)) {
            membersList.remove(member);
            competitionMembersListRepository.saveAndFlush(list);
            LOG.info("Usunięto Klubowicza do Listy");
            historyService.removeCompetitionRecord(member.getUuid(),list);
            return true;
        }
        return false;
    }
}
