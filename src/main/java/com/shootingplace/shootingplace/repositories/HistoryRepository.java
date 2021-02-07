package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.HistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<HistoryEntity, String> {
}
