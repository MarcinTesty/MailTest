package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.ChangeHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeHistoryRepository extends JpaRepository<ChangeHistoryEntity, String> {
}
