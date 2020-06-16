package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.ContributionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContributionRepository extends JpaRepository<ContributionEntity, UUID> {

}
