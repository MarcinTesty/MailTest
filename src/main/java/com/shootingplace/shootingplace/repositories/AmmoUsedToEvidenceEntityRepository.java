package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.AmmoUsedToEvidenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AmmoUsedToEvidenceEntityRepository extends JpaRepository<AmmoUsedToEvidenceEntity, UUID> {
}
