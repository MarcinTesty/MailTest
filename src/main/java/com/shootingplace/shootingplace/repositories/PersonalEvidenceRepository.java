package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.PersonalEvidenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalEvidenceRepository extends JpaRepository<PersonalEvidenceEntity, String> {
}
