package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.JudgingHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JudgingHistoryRepository extends JpaRepository<JudgingHistoryEntity, UUID> {
}
