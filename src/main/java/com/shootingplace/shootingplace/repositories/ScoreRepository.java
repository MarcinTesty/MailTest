package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.ScoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreRepository extends JpaRepository<ScoreEntity, String> {
}
