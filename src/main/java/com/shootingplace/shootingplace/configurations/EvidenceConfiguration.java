package com.shootingplace.shootingplace.configurations;

import com.shootingplace.shootingplace.domain.entities.ClubEntity;
import com.shootingplace.shootingplace.repositories.ClubRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EvidenceConfiguration {

    private final ClubRepository clubRepository;

    public EvidenceConfiguration(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @Bean
    public CommandLineRunner initCLub() {
        return args ->
        clubRepository.saveAndFlush(ClubEntity.builder()
                .id(1)
                .licenseNumber("1233/2020")
                .name("Klub Strzelecki Dziesiątka LOK Łódź")
                .build());
    }
}
