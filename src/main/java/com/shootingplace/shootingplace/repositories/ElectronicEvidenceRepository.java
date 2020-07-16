package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.ElectronicEvidenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ElectronicEvidenceRepository extends JpaRepository<ElectronicEvidenceEntity, Integer> {
}
