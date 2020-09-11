package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.PersonalEvidenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PersonalEvidenceRepository extends JpaRepository<PersonalEvidenceEntity, UUID> {
}
