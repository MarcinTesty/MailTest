package com.shootingplace.shootingplace.configurations;

import com.shootingplace.shootingplace.domain.entities.ClubEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EvidenceConfiguration {

    @Bean
    public CommandLineRunner initCLub() {
        return args ->
                ClubEntity.builder()
                        .id(1)
                        .licenseNumber("1233/2020")
                        .name("Klub Strzelecki Dziesiątka LOK Łódź")
                        .build();
    }
}
