package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.DisciplineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DisciplineRepository extends JpaRepository<DisciplineEntity, UUID> {
}
