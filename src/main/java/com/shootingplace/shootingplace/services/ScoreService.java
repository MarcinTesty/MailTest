package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.CompetitionMembersListEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.entities.OtherPersonEntity;
import com.shootingplace.shootingplace.domain.entities.ScoreEntity;
import com.shootingplace.shootingplace.repositories.CompetitionMembersListRepository;
import com.shootingplace.shootingplace.repositories.ScoreRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;

@Service
public class ScoreService {

    private final ScoreRepository scoreRepository;
    private final CompetitionMembersListRepository competitionMembersListRepository;

    public ScoreService(ScoreRepository scoreRepository, CompetitionMembersListRepository competitionMembersListRepository) {
        this.scoreRepository = scoreRepository;
        this.competitionMembersListRepository = competitionMembersListRepository;
    }

    ScoreEntity createScore(float score, float innerTen, float outerTen, String competitionMembersListEntityUUID, MemberEntity memberEntity, OtherPersonEntity otherPersonEntity) {
        String name;
        if(memberEntity!=null){
            name = memberEntity.getSecondName()+ " " + memberEntity.getFirstName();
        }
        else {
            name = otherPersonEntity.getSecondName() + " " + otherPersonEntity.getFirstName();
        }
        return scoreRepository.saveAndFlush(ScoreEntity.builder()
                .competitionMembersListEntityUUID(competitionMembersListEntityUUID)
                .member(memberEntity)
                .otherPersonEntity(otherPersonEntity)
                .score(score)
                .innerTen(innerTen)
                .outerTen(outerTen)
                .ammunition(false)
                .name(name)
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
