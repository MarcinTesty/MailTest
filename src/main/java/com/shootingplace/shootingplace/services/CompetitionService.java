package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.CompetitionEntity;
import com.shootingplace.shootingplace.domain.models.Competition;
import com.shootingplace.shootingplace.repositories.CompetitionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final Logger LOG = LogManager.getLogger(getClass());


    public CompetitionService(CompetitionRepository competitionRepository) {
        this.competitionRepository = competitionRepository;
    }

    public List<CompetitionEntity> getAllCompetitions() {
        if (competitionRepository.findAll().isEmpty()) {
            createCompetitions();
            LOG.info("Zostały utworzone domyślne encje Konkurencji");
        }
        LOG.info("Wyświetlono listę Konkurencji");
        return competitionRepository.findAll();

    }

    private void createCompetitions() {

        competitionRepository.saveAndFlush(CompetitionEntity.builder()
                .uuid(UUID.randomUUID())
                .name("Pistolet sportowy")
                .build());
        competitionRepository.saveAndFlush(CompetitionEntity.builder()
                .uuid(UUID.randomUUID())
                .name("Pistolet centralnego zapłonu")
                .build());
        competitionRepository.saveAndFlush(CompetitionEntity.builder()
                .uuid(UUID.randomUUID())
                .name("Pistolet pneumatyczny")
                .build());
        competitionRepository.saveAndFlush(CompetitionEntity.builder()
                .uuid(UUID.randomUUID())
                .name("Pistolet dynamiczny")
                .build());
        competitionRepository.saveAndFlush(CompetitionEntity.builder()
                .uuid(UUID.randomUUID())
                .name("Pistolet dowolny")
                .build());
        competitionRepository.saveAndFlush(CompetitionEntity.builder()
                .uuid(UUID.randomUUID())
                .name("Karabin dowolny")
                .build());
        competitionRepository.saveAndFlush(CompetitionEntity.builder()
                .uuid(UUID.randomUUID())
                .name("Karabin pneumatyczny")
                .build());
        competitionRepository.saveAndFlush(CompetitionEntity.builder()
                .uuid(UUID.randomUUID())
                .name("Strzelba dynamiczna")
                .build());
        LOG.info("Stworzono encje konkurencji");
    }

    public boolean createNewCompetition(Competition competition) {
        List<String> list = new ArrayList<>();
        competitionRepository.findAll().forEach(e-> list.add(e.getName()));
        String s = competition.getName().substring(0,1).toUpperCase();
        String s1 = competition.getName().substring(1).toLowerCase();
        if (list.isEmpty()){
            createCompetitions();
        }
        if (list.contains(s+s1)){
            LOG.info("Taka konkurencja już istnieje");
            return false;
        }
        competition.setName(s+s1);
        CompetitionEntity competitionEntity = Mapping.map(competition);
        competitionRepository.saveAndFlush(competitionEntity);
        LOG.info("Utworzono nową konkurencję " + competition.getName());
        return true;
    }

    public void deleteCompetition(UUID competitionUUID) {
        CompetitionEntity competitionEntity = competitionRepository.findById(competitionUUID).orElseThrow(EntityNotFoundException::new);
        competitionRepository.delete(competitionEntity);
        LOG.info("Usunięto konkurencję bo była ujowa");

    }
}
