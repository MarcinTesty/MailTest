package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.*;
import com.shootingplace.shootingplace.repositories.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class CompetitionMembersListService {

    private final MemberRepository memberRepository;
    private final CompetitionMembersListRepository competitionMembersListRepository;
    private final HistoryService historyService;
    private final OtherPersonRepository otherPersonRepository;
    private final ScoreService scoreService;
    private final ScoreRepository scoreRepository;
    private final TournamentRepository tournamentRepository;
    private final Logger LOG = LogManager.getLogger();


    public CompetitionMembersListService(MemberRepository memberRepository, CompetitionMembersListRepository competitionMembersListRepository, HistoryService historyService, OtherPersonRepository otherPersonRepository, ScoreService scoreService, ScoreRepository scoreRepository, TournamentRepository tournamentRepository) {
        this.memberRepository = memberRepository;
        this.competitionMembersListRepository = competitionMembersListRepository;
        this.historyService = historyService;
        this.otherPersonRepository = otherPersonRepository;
        this.scoreService = scoreService;
        this.scoreRepository = scoreRepository;
        this.tournamentRepository = tournamentRepository;
    }

    public boolean addScoreToCompetitionList(String competitionUUID, int legitimationNumber, int otherPerson) {
        CompetitionMembersListEntity list = competitionMembersListRepository.findById(competitionUUID).orElseThrow(EntityNotFoundException::new);
        List<ScoreEntity> scoreList = list.getScoreList();


        if (legitimationNumber > 0) {
            MemberEntity member = memberRepository.findAll().stream().filter(f -> f.getLegitimationNumber().equals(legitimationNumber)).findFirst().orElse(null);
            boolean match = scoreList.stream().anyMatch(f -> f.getMember() == member);
            if (match) {
                LOG.info("Nie mo??na doda?? bo osoba ju?? si?? znajduje na li??cie");
                return false;
            } else {
                ScoreEntity score = scoreService.createScore(0, 0, 0, competitionUUID, member, null);
                scoreList.add(score);
                scoreList.sort(Comparator.comparing(ScoreEntity::getScore).reversed().thenComparing(ScoreEntity::getInnerTen).reversed().thenComparing(ScoreEntity::getOuterTen).reversed());
                competitionMembersListRepository.saveAndFlush(list);
                LOG.info("Dodano Klubowicza do Listy");
                if (member != null) {
                    historyService.addCompetitionRecord(member.getUuid(), list);
                }
                return true;
            }
        }
        if (otherPerson > 0) {
            OtherPersonEntity otherPersonEntity = otherPersonRepository.findAll().stream().filter(f -> f.getId().equals(otherPerson)).findFirst().orElse(null);
            if (otherPersonEntity != null) {
                boolean match1 = scoreList.stream().anyMatch(a -> a.getOtherPersonEntity() == otherPersonEntity);
                if (match1) {
                    LOG.info("Nie mo??na doda?? bo osoba ju?? si?? znajduje na li??cie");
                    return false;
                } else {
                    ScoreEntity score = scoreService.createScore(0, 0, 0, competitionUUID, null, otherPersonEntity);
                    scoreList.add(score);
                    scoreList.sort(Comparator.comparing(ScoreEntity::getScore).reversed().thenComparing(ScoreEntity::getInnerTen).reversed().thenComparing(ScoreEntity::getOuterTen).reversed());
                    LOG.info("Dodano Obcego Zawodnika do Listy");
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
        MemberEntity member = memberRepository.findAll().stream().filter(f -> f.getLegitimationNumber().equals(legitimationNumber)).findFirst().orElseThrow(EntityNotFoundException::new);
        OtherPersonEntity otherPersonEntity = otherPersonRepository.findAll().stream().filter(f -> f.getId().equals(otherPerson)).findFirst().orElseThrow(EntityNotFoundException::new);
        List<ScoreEntity> scoreList = list.getScoreList();

        if (legitimationNumber > 0) {
            if (scoreList.stream().anyMatch(f -> f.getMember() == member)) {
                ScoreEntity score = scoreList.stream().filter(f -> f.getMember() == member).findFirst().orElseThrow(EntityNotFoundException::new);
                scoreList.remove(score);
                scoreRepository.saveAndFlush(score);

                competitionMembersListRepository.saveAndFlush(list);
                LOG.info("Usuni??to osob?? do Listy");
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
                LOG.info("Usuni??to osob?? z Listy");
                return true;

            } else {
                return false;
            }
        }
        return false;
    }

//    public boolean sortScore(String competitionUUID, boolean sort) {
//        List<ScoreEntity> scoreList = competitionMembersListRepository.findById(competitionUUID).orElseThrow(EntityNotFoundException::new).getScoreList();
//        if (sort) {
//            scoreList.sort(Comparator.comparing(ScoreEntity::getName));
//        } else {
//            scoreList.sort(Comparator.comparing(ScoreEntity::getScore)
//                    .reversed()
//                    .thenComparing(Comparator.comparing(ScoreEntity::getInnerTen)
//                            .reversed()
//                            .thenComparing(Comparator.comparing(ScoreEntity::getOuterTen)
//                                    .reversed().thenComparing(ScoreEntity::getName)
//                            )));
//        }
//        competitionMembersListRepository.saveAndFlush(competitionMembersListRepository.findById(competitionUUID).orElseThrow(EntityNotFoundException::new));
//
//        return true;
//    }

    public String getIDByName(String name, String tournamentUUID) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);
        return "\"" + tournamentEntity.getCompetitionsList().stream().filter(f -> f.getName().equals(name)).findFirst().orElseThrow(EntityNotFoundException::new).getUuid() + "\"";
    }

    public boolean removeListFromTournament(String tournamentUUID, String competitionUUID) {
        CompetitionMembersListEntity competitionMembersListEntity = competitionMembersListRepository.findById(competitionUUID).orElseThrow(EntityNotFoundException::new);
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);

        if (competitionMembersListEntity.getScoreList().isEmpty()) {
            tournamentEntity.getCompetitionsList().remove(competitionMembersListEntity);
            tournamentRepository.saveAndFlush(tournamentEntity);
            competitionMembersListRepository.delete(competitionMembersListEntity);
            return true;
        } else {
            return false;
        }

    }

    public List<String> getMemberStartsInTournament(String memberUUID, int otherID, String tournamentUUID) {

        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentUUID).orElseThrow(EntityNotFoundException::new);
        List<String> list = new ArrayList<>();

        if (otherID > 0) {
            OtherPersonEntity otherPersonEntity = otherPersonRepository.findById(otherID).orElseThrow(EntityNotFoundException::new);

            tournamentEntity.getCompetitionsList().forEach(e -> e.getScoreList().stream().filter(f->f.getOtherPersonEntity()!=null).forEach(g -> {
                if (g.getOtherPersonEntity().getId().equals(otherPersonEntity.getId())) {
                    list.add(e.getName());
                }
            }));
        } else {
            MemberEntity memberEntity = memberRepository.findById(memberUUID).orElseThrow(EntityNotFoundException::new);

            tournamentEntity.getCompetitionsList().forEach(e -> e.getScoreList().stream().filter(f->f.getMember()!=null).forEach(g -> {
                if (g.getMember().getUuid().equals(memberEntity.getUuid())) {
                    list.add(e.getName());
                }
            }));
        }
        return list;

    }
}
