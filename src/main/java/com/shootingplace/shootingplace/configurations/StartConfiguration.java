package com.shootingplace.shootingplace.configurations;

import com.shootingplace.shootingplace.repositories.ClubRepository;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartConfiguration {

    private final ClubRepository clubRepository;

    public StartConfiguration(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

}
