package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.GunEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GunRepository extends JpaRepository<GunEntity,String> {
}
