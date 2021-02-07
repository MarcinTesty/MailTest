package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.WeaponPermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeaponPermissionRepository extends JpaRepository<WeaponPermissionEntity, String> {
    Optional<WeaponPermissionEntity> findByNumber(String number);
    Optional<WeaponPermissionEntity> findByAdmissionToPossessAWeapon(String number);
}
