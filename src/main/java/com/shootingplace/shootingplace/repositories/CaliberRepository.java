package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.CaliberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CaliberRepository extends JpaRepository<CaliberEntity, UUID> {
}
