package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.ScoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ScoreRepository extends JpaRepository<ScoreEntity, UUID> {
}
