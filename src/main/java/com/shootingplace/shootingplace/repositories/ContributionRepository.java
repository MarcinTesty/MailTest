package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.ContributionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContributionRepository extends JpaRepository<ContributionEntity, String> {

}
