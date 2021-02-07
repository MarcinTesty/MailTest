package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.*;
import com.shootingplace.shootingplace.repositories.CompetitionMembersListRepository;
import com.shootingplace.shootingplace.repositories.ScoreRepository;
import com.shootingplace.shootingplace.repositories.TournamentRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@Service
public class ScoreService {

    private final ScoreRepository scoreRepository;
    private final CompetitionMembersListRepository competitionMembersListRepository;
    private final TournamentRepository tournamentRepository;

    public ScoreService(ScoreRepository scoreRepository, CompetitionMembersListRepository competitionMembersListRepository, TournamentRepository tournamentRepository) {
        this.scoreRepository = scoreRepository;
        this.competitionMembersListRepository = competitionMembersListRepository;
        this.tournamentRepository = tournamentRepository;
    }

    ScoreEntity createScore(float score, float innerTen, float outerTen, String competitionMembersListEntityUUID, MemberEntity memberEntity, OtherPersonEntity otherPersonEntity) {
        String name;
        if (memberEntity != null) {
            name = memberEntity.getSecondName() + " " + memberEntity.getFirstName();
        } else {
            name = otherPersonEntity.getSecondName() + " " + otherPersonEntity.getFirstName();
        }
        int number = 0;


        CompetitionMembersListEntity competitionMembersListEntity = competitionMembersListRepository.findById(competitionMembersListEntityUUID)
                .orElseThrow(EntityNotFoundException::new);

        TournamentEntity tournamentEntity = tournamentRepository.findById(competitionMembersListEntity.getAttachedToTournament()).orElseThrow(EntityNotFoundException::new);


        List<ScoreEntity> scoreEntityList = new ArrayList<>();

        tournamentEntity.getCompetitionsList().forEach(e -> scoreEntityList.addAll(e.getScoreList()));

        ScoreEntity scoreEntity = scoreEntityList.stream().max(Comparator.comparing(ScoreEntity::getMetricNumber)).orElse(null);

        boolean match = scoreEntityList.stream().anyMatch(a->a.getMember()==(memberEntity));

        boolean match1 = scoreEntityList.stream().anyMatch(a->a.getOtherPersonEntity()==(otherPersonEntity));

        if (memberEntity != null) {
            if (match) {
                number = scoreEntityList.stream().filter(f -> f.getMember()==(memberEntity)).findFirst().get().getMetricNumber();
            }
            else{
                if(scoreEntity != null){
                number = scoreEntity.getMetricNumber()+1;

                }else {
                    number = 1;
                }
            }
        }
        if (otherPersonEntity != null) {
            if (match1) {
                number = scoreEntityList.stream().filter(f -> f.getOtherPersonEntity()==(otherPersonEntity)).findFirst().get().getMetricNumber();
            }
            else{
                if(scoreEntity != null){
                    number = scoreEntity.getMetricNumber()+1;

                }else {
                    number = 1;
                }
            }
        }
//// cały czas któreś nie działa i nie wiem czemu...
//        if (otherPersonEntity != null) {
//
//            // chodzi o ten warunek...
//            if (scoreEntityList.stream().anyMatch(e -> e.getOtherPersonEntity().equals(otherPersonEntity))) {
//                System.out.println("znaleziono");
//                number = scoreEntityList.stream().filter(f -> f.getOtherPersonEntity().equals(otherPersonEntity)).findFirst().get().getMetricNumber();
//                System.out.println(number);
//            } else {
//                System.out.println("nie znaleziono");
//                number = scoreEntity.getMetricNumber() + 1;
//            }
//        }
//        if (memberEntity != null) {
//
//            // lub ten warunek...
//            // jeśli na liście są już membery to nie mogę dodać othera i odwrotnie
//            // DLACZEGO?!?
//            // NullPointerException
//            if (scoreEntityList.stream().anyMatch(e -> e.getMember().equals(memberEntity))) {
//                System.out.println("znaleziono");
//                number = scoreEntityList.stream().filter(f -> f.getMember().equals(memberEntity)).findFirst().get().getMetricNumber();
//                System.out.println(number);
//            } else {
//                System.out.println("nie znaleziono");
//                number = scoreEntity.getMetricNumber() + 1;
//            }
//
//
//        }

        return scoreRepository.saveAndFlush(ScoreEntity.builder()
                .competitionMembersListEntityUUID(competitionMembersListEntityUUID)
                .member(memberEntity)
                .otherPersonEntity(otherPersonEntity)
                .score(score)
                .innerTen(innerTen)
                .outerTen(outerTen)
                .ammunition(false)
                .name(name)
                .metricNumber(number)
                .build());

    }

    public boolean setScore(String scoreUUID, float score, float innerTen, float outerTen) {
        ScoreEntity scoreEntity = scoreRepository.findById(scoreUUID).orElseThrow(EntityNotFoundException::new);
        if (score == -1) {
            score = scoreEntity.getScore();
        }
        if (innerTen == -1) {
            innerTen = scoreEntity.getInnerTen();
        }
        if (outerTen == -1) {
            outerTen = scoreEntity.getOuterTen();
        }
        scoreEntity.setScore(score);
        scoreEntity.setInnerTen(innerTen);
        scoreEntity.setOuterTen(outerTen);
        scoreRepository.saveAndFlush(scoreEntity);
        String competitionMembersListEntityUUID = scoreEntity.getCompetitionMembersListEntityUUID();
        CompetitionMembersListEntity competitionMembersListEntity = competitionMembersListRepository.findById(competitionMembersListEntityUUID).orElseThrow(EntityNotFoundException::new);
        List<ScoreEntity> scoreList = competitionMembersListEntity.getScoreList();
        scoreList.sort(Comparator.comparing(ScoreEntity::getScore)
                .reversed()
                .thenComparing(Comparator.comparing(ScoreEntity::getOuterTen)
                        .reversed()
                        .thenComparing(Comparator.comparing(ScoreEntity::getInnerTen)
                                .reversed())
                ));
        competitionMembersListRepository.saveAndFlush(competitionMembersListEntity);
        return true;
    }

    public boolean toggleAmmunitionInScore(String scoreUUID) {
        ScoreEntity scoreEntity = scoreRepository.findById(scoreUUID).orElseThrow(EntityNotFoundException::new);
        scoreEntity.toggleAmmunition();
        scoreRepository.saveAndFlush(scoreEntity);
        return true;
    }
}
