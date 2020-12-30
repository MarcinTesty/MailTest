package com.shootingplace.shootingplace.configurations;

import com.shootingplace.shootingplace.domain.entities.ClubEntity;
import com.shootingplace.shootingplace.repositories.ClubRepository;
import com.shootingplace.shootingplace.services.CompetitionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartConfiguration {

    private final ClubRepository clubRepository;
    private final CompetitionService competitionService;


    public StartConfiguration(ClubRepository clubRepository, CompetitionService competitionService) {
        this.clubRepository = clubRepository;
        this.competitionService = competitionService;
    }

    @Bean
    public CommandLineRunner initClub() {
        competitionService.createCompetitions();
        return args ->
                clubRepository.saveAndFlush(ClubEntity.builder()
                        .id(1)
                        .name("DZIESIĄTKA LOK ŁÓDŹ")
                        .build());
    }

}
