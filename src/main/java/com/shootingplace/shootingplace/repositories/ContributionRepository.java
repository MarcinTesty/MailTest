package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.ContributionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
public interface ContributionRepository extends JpaRepository<ContributionEntity, UUID> {

}
