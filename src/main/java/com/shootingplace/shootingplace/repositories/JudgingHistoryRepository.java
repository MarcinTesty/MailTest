package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.JudgingHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JudgingHistoryRepository extends JpaRepository<JudgingHistoryEntity, String> {
}
