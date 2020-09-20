package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.CompetitionEntity;
import com.shootingplace.shootingplace.domain.models.Competition;
import com.shootingplace.shootingplace.repositories.CompetitionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
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
            LOG.info("Została utworzone domyślne encje Konkurencji");
        }
        System.out.println(competitionRepository.findAll());
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

        CompetitionEntity competitionEntity = Mapping.map(competition);
        competitionRepository.saveAndFlush(competitionEntity);
        return true;
    }

    public void deleteCompetition(UUID competitionUUID) {
        CompetitionEntity competitionEntity = competitionRepository.findById(competitionUUID).orElseThrow(EntityNotFoundException::new);
        competitionRepository.delete(competitionEntity);
        LOG.info("Usunięto konkurencję bo była ujowa");

    }
}
