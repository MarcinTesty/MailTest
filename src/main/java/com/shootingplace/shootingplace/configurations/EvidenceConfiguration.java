package com.shootingplace.shootingplace.configurations;

import com.shootingplace.shootingplace.domain.entities.ClubEntity;
import com.shootingplace.shootingplace.repositories.ClubRepository;
import com.shootingplace.shootingplace.services.AmmoEvidenceService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EvidenceConfiguration {

    private final ClubRepository clubRepository;
    private final AmmoEvidenceService ammoEvidenceService;


    public EvidenceConfiguration(ClubRepository clubRepository, AmmoEvidenceService ammoEvidenceService) {
        this.clubRepository = clubRepository;
        this.ammoEvidenceService = ammoEvidenceService;
    }

    @Bean
    public CommandLineRunner initClub() {
        return args ->
                clubRepository.saveAndFlush(ClubEntity.builder()
                        .id(1)
                        .name("Klub Strzelecki Dziesiątka LOK Łódź")
                        .build());
    }
//    @Bean
//    public CommandLineRunner initAmmoList() {
//        return args ->
//                ammoEvidenceService.addAmmoEvidenceEntity();
//
//    }

}
