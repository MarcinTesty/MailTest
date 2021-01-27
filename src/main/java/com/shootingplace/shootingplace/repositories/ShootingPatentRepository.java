package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.ShootingPatentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShootingPatentRepository extends JpaRepository<ShootingPatentEntity, String> {
    Optional<ShootingPatentEntity> findByPatentNumber(String number);

    List<ShootingPatentEntity> findByPatentNumberIsNotNull();

}
