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
import java.util.UUID;

@Service
public class ScoreService {

    private final ScoreRepository scoreRepository;
    private final CompetitionMembersListRepository competitionMembersListRepository;

    public ScoreService(ScoreRepository scoreRepository, CompetitionMembersListRepository competitionMembersListRepository) {
        this.scoreRepository = scoreRepository;
        this.competitionMembersListRepository = competitionMembersListRepository;
    }

    ScoreEntity createScore(float score, float innerTen, float outerTen, UUID competitionMembersListEntityUUID, MemberEntity memberEntity, OtherPersonEntity otherPersonEntity) {

        return scoreRepository.saveAndFlush(ScoreEntity.builder()
                .competitionMembersListEntityUUID(competitionMembersListEntityUUID)
                .member(memberEntity)
                .otherPersonEntity(otherPersonEntity)
                .score(score)
                .innerTen(innerTen)
                .outerTen(outerTen)
                .build());

    }

    public boolean setScore(UUID scoreUUID, float score, float innerTen, float outerTen) {
        ScoreEntity scoreEntity = scoreRepository.findById(scoreUUID).orElseThrow(EntityNotFoundException::new);
        scoreEntity.setScore(score);
        scoreEntity.setInnerTen(innerTen);
        scoreEntity.setOuterTen(outerTen);
        scoreRepository.saveAndFlush(scoreEntity);
        UUID competitionMembersListEntityUUID = scoreEntity.getCompetitionMembersListEntityUUID();
        CompetitionMembersListEntity competitionMembersListEntity = competitionMembersListRepository.findById(competitionMembersListEntityUUID).orElseThrow(EntityNotFoundException::new);
        List<ScoreEntity> scoreList = competitionMembersListEntity.getScoreList();
        scoreList.sort(Comparator.comparing(ScoreEntity::getScore).reversed().thenComparing(Comparator.comparing(ScoreEntity::getInnerTen).reversed().thenComparing(Comparator.comparing(ScoreEntity::getOuterTen).reversed())));
        competitionMembersListRepository.saveAndFlush(competitionMembersListEntity);
        return true;
    }
}
