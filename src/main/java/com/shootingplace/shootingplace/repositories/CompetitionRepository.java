package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.CompetitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompetitionRepository extends JpaRepository<CompetitionEntity, UUID> {
}
