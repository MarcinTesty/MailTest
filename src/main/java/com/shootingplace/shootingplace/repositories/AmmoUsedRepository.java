package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.AmmoUsedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AmmoUsedRepository extends JpaRepository<AmmoUsedEntity, UUID> {
}
