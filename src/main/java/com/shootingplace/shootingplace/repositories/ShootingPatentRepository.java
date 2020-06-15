package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.ShootingPatentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShootingPatentRepository extends JpaRepository<ShootingPatentEntity, UUID> {
    Optional<ShootingPatentEntity> findByPatentNumber(String number);

    List<ShootingPatentEntity> findByPatentNumberIsNotNull();

}
