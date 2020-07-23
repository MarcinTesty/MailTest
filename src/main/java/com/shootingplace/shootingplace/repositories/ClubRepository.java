package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.ClubEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ClubRepository extends JpaRepository<ClubEntity, Integer> {
}
