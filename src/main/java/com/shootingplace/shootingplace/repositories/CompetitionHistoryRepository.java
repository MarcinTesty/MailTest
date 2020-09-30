package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.CompetitionHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompetitionHistoryRepository extends JpaRepository<CompetitionHistoryEntity, UUID> {
}
