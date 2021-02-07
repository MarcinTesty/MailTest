package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.CompetitionMembersListEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.entities.OtherPersonEntity;
import com.shootingplace.shootingplace.domain.entities.ScoreEntity;
import com.shootingplace.shootingplace.repositories.CompetitionMembersListRepository;
import com.shootingplace.shootingplace.repositories.MemberRepository;
import com.shootingplace.shootingplace.repositories.OtherPersonRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;

@Service
public class CompetitionMembersListService {

    private final MemberRepository memberRepository;
    private final CompetitionMembersListRepository competitionMembersListRepository;
    private final HistoryService historyService;
    private final OtherPersonRepository otherPersonRepository;
    private final ScoreService scoreService;
    private final Logger LOG = LogManager.getLogger();


    public CompetitionMembersListService(MemberRepository memberRepository, CompetitionMembersListRepository competitionMembersListRepository, HistoryService historyService, OtherPersonRepository otherPersonRepository, ScoreService scoreService) {
        this.memberRepository = memberRepository;
        this.competitionMembersListRepository = competitionMembersListRepository;
        this.historyService = historyService;
        this.otherPersonRepository = otherPersonRepository;
        this.scoreService = scoreService;
    }

    public boolean addScoreToCompetitionList(String competitionUUID, int legitimationNumber, int otherPerson) {
        CompetitionMembersListEntity list = competitionMembersListRepository.findById(competitionUUID).orElseThrow(EntityNotFoundException::new);
        List<ScoreEntity> scoreList = list.getScoreList();


        if (legitimationNumber > 0) {
            MemberEntity member = memberRepository.findAll().stream().filter(f -> f.getLegitimationNumber().equals(legitimationNumber)).findFirst().orElse(null);
            boolean match = scoreList.stream().anyMatch(f -> f.getMember() == member);
            if (match) {
                LOG.info("Nie można dodać bo osoba już się znajduje na liście");
                return false;
            } else {
                ScoreEntity score = scoreService.createScore(0, 0, 0, competitionUUID, member, null);
                scoreList.add(score);
                scoreList.sort(Comparator.comparing(ScoreEntity::getScore).reversed());
                competitionMembersListRepository.saveAndFlush(list);
                LOG.info("Dodano Klubowicza do Listy");
                historyService.addCompetitionRecord(member.getUuid(), list);
                return true;
            }
        }
        if (otherPerson > 0) {
            OtherPersonEntity otherPersonEntity = otherPersonRepository.findAll().stream().filter(f -> f.getId().equals(otherPerson)).findFirst().orElse(null);
            if (otherPersonEntity != null) {
                boolean match1 = scoreList.stream().anyMatch(a -> a.getOtherPersonEntity() == otherPersonEntity);
                if (match1) {
                    LOG.info("Nie można dodać bo osoba już się znajduje na liście");
                    return false;
                } else {
                    ScoreEntity score = scoreService.createScore(0, 0, 0, competitionUUID, null, otherPersonEntity);
                    scoreList.add(score);
                    scoreList.sort(Comparator.comparing(ScoreEntity::getScore).reversed());
                    LOG.info("Dodano Obcego Klubowicza do Listy");
                    competitionMembersListRepository.saveAndFlush(list);
                    return true;
                }
            }
        } else {
            return false;
        }
        return false;
    }


    public boolean removeScoreFromList(String competitionUUID, int legitimationNumber, int otherPerson) {
        CompetitionMembersListEntity list = competitionMembersListRepository.findById(competitionUUID).orElseThrow(EntityNotFoundException::new);
        MemberEntity member = memberRepository.findAll().stream().filter(f -> f.getLegitimationNumber().equals(legitimationNumber)).findFirst().orElse(null);
        OtherPersonEntity otherPersonEntity = otherPersonRepository.findAll().stream().filter(f -> f.getId().equals(otherPerson)).findFirst().orElse(null);
        List<ScoreEntity> scoreList = list.getScoreList();

        if (legitimationNumber > 0) {
            if (scoreList.stream().anyMatch(f -> f.getMember() == member)) {
                ScoreEntity score = scoreList.stream().filter(f -> f.getMember() == member).findFirst().orElseThrow(EntityNotFoundException::new);
                scoreList.remove(score);
                competitionMembersListRepository.saveAndFlush(list);
                LOG.info("Usunięto osobę do Listy");
                historyService.removeCompetitionRecord(member.getUuid(), list);

                return true;
            } else {
                return false;
            }
        }
        if (otherPerson > 0) {
            if (scoreList.stream().anyMatch(f -> f.getOtherPersonEntity() == otherPersonEntity)) {
                ScoreEntity score = scoreList.stream().filter(f -> f.getOtherPersonEntity() == otherPersonEntity).findFirst().orElseThrow(EntityNotFoundException::new);
                scoreList.remove(score);
                competitionMembersListRepository.saveAndFlush(list);
                LOG.info("Usunięto osobę z Listy");
                return true;

            } else {
                return false;
            }
        }
        return false;
    }

    public boolean sortScore(String competitionUUID, boolean sort) {
        List<ScoreEntity> scoreList = competitionMembersListRepository.findById(competitionUUID).orElseThrow(EntityNotFoundException::new).getScoreList();
        if (sort) {
            scoreList.sort(Comparator.comparing(ScoreEntity::getName));
        } else {
            scoreList.sort(Comparator.comparing(ScoreEntity::getScore)
                    .reversed()
                    .thenComparing(Comparator.comparing(ScoreEntity::getOuterTen)
                            .reversed()
                            .thenComparing(Comparator.comparing(ScoreEntity::getInnerTen)
                                    .reversed().thenComparing(ScoreEntity::getName)
                            )));
        }
        competitionMembersListRepository.saveAndFlush(competitionMembersListRepository.findById(competitionUUID).orElseThrow(EntityNotFoundException::new));

        return true;
    }
}
