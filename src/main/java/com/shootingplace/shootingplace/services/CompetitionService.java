package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.CompetitionEntity;
import com.shootingplace.shootingplace.domain.enums.Discipline;
import com.shootingplace.shootingplace.repositories.CompetitionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
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
        List<CompetitionEntity> competitionEntityList = competitionRepository.findAll();

        if (competitionEntityList.isEmpty()) {
            createCompetitions();
            LOG.info("Zostały utworzone domyślne encje Konkurencji");
        }
        LOG.info("Wyświetlono listę Konkurencji");
        competitionEntityList.sort(Comparator.comparing(CompetitionEntity::getDiscipline));
        return competitionEntityList;
    }

    private void createCompetitions() {

        competitionRepository.saveAndFlush(CompetitionEntity.builder()
                .uuid(UUID.randomUUID().toString())
                .name("25m Pistolet sportowy bocznego zapłonu 10 strzałów OPEN")
                .discipline(Discipline.PISTOL.getName())
                .build());
        competitionRepository.saveAndFlush(CompetitionEntity.builder()
                .uuid(UUID.randomUUID().toString())
                .name("25m Pistolet centralnego zapłonu 10 strzałów OPEN")
                .discipline(Discipline.PISTOL.getName())
                .build());
        competitionRepository.saveAndFlush(CompetitionEntity.builder()
                .uuid(UUID.randomUUID().toString())
                .name("10m Pistolet pneumatyczny 10 strzałów OPEN")
                .discipline(Discipline.PISTOL.getName())
                .build());
        competitionRepository.saveAndFlush(CompetitionEntity.builder()
                .uuid(UUID.randomUUID().toString())
                .name("50m Pistolet dowolny bocznego zapłonu 10 strzałów OPEN")
                .discipline(Discipline.PISTOL.getName())
                .build());
        competitionRepository.saveAndFlush(CompetitionEntity.builder()
                .uuid(UUID.randomUUID().toString())
                .name("10m Karabin pneumatyczny 10 strzałów OPEN")
                .discipline(Discipline.RIFLE.getName())
                .build());
        competitionRepository.saveAndFlush(CompetitionEntity.builder()
                .uuid(UUID.randomUUID().toString())
                .name("10m Strzelba dynamiczna 7 strzałów OPEN")
                .discipline(Discipline.SHOTGUN.getName())
                .build());
        competitionRepository.saveAndFlush(CompetitionEntity.builder()
                .uuid(UUID.randomUUID().toString())
                .name("10m Strzelba statyczna 7 strzałów OPEN")
                .discipline(Discipline.SHOTGUN.getName())
                .build());
        LOG.info("Stworzono encje konkurencji");
    }

    public boolean createNewCompetition(String name, String discipline) {
        List<String> list = new ArrayList<>();
        competitionRepository.findAll().forEach(e -> list.add(e.getName()));
        if (list.isEmpty()) {
            createCompetitions();
        }
        if (list.contains(name)) {
            LOG.info("Taka konkurencja już istnieje");
            return false;
        }
        if (discipline.equals(Discipline.PISTOL.getName()) || discipline.equals(Discipline.RIFLE.getName()) || discipline.equals(Discipline.SHOTGUN.getName())) {
            CompetitionEntity competitionEntity = CompetitionEntity.builder()
                    .name(name)
                    .discipline(discipline)
                    .build();
            competitionRepository.saveAndFlush(competitionEntity);
            LOG.info("Utworzono nową konkurencję \" " + name + " \"");
            return true;
        } else {
            return false;
        }
    }

    public void deleteCompetition(String competitionUUID) {
        CompetitionEntity competitionEntity = competitionRepository.findById(competitionUUID).orElseThrow(EntityNotFoundException::new);
        competitionRepository.delete(competitionEntity);
        LOG.info("Usunięto konkurencję bo była ujowa");

    }
}
