package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.RecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecordRepository extends JpaRepository<RecordEntity, UUID> {
}
