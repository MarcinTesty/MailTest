package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.CaliberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaliberRepository extends JpaRepository<CaliberEntity, String> {
}
