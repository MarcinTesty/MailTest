package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.AmmoUsedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmmoUsedRepository extends JpaRepository<AmmoUsedEntity, String> {
}
