package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.HistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HistoryRepository extends JpaRepository<HistoryEntity, UUID> {
}
