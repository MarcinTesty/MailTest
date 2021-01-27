package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.LicenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LicenseRepository extends JpaRepository<LicenseEntity, String> {
    Optional<LicenseEntity> findByNumber(String number);

    List<LicenseEntity> findAllByNumberIsNotNull();
}
