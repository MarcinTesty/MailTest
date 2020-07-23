package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.LicenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LicenseRepository extends JpaRepository<LicenseEntity, UUID> {
    Optional<LicenseEntity> findByNumber(String number);

    List<LicenseEntity> findAllByNumberIsNotNull();
}
