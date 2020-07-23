package com.shootingplace.shootingplace;

import com.shootingplace.shootingplace.domain.entities.ElectronicEvidenceEntity;
import com.shootingplace.shootingplace.repositories.ElectronicEvidenceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class ShootingplaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShootingplaceApplication.class, args);
	}
	@Bean
	public CommandLineRunner initEvidence() {
		return args ->
			ElectronicEvidenceEntity.builder()
					.id(1)
					.members(null)
					.date(LocalDate.now())
					.others(null)
					.build();

	}

}
