package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.AmmoEvidenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmmoEvidenceRepository extends JpaRepository<AmmoEvidenceEntity, String> {
}
