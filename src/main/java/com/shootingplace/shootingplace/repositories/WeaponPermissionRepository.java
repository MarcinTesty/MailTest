package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.WeaponPermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WeaponPermissionRepository extends JpaRepository<WeaponPermissionEntity, UUID> {
    Optional<WeaponPermissionEntity> findByNumber(String number);
}
