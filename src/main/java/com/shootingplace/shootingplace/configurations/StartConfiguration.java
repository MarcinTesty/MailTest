package com.shootingplace.shootingplace.configurations;

import com.shootingplace.shootingplace.domain.entities.ClubEntity;
import com.shootingplace.shootingplace.repositories.ClubRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartConfiguration {

    private final ClubRepository clubRepository;

    public StartConfiguration(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @Bean
    public CommandLineRunner initClub() {
        return args ->
                clubRepository.saveAndFlush(ClubEntity.builder()
                        .id(1)
                        .name("DZIESIĄTKA LOK ŁÓDŹ")
                        .build());
    }

}
