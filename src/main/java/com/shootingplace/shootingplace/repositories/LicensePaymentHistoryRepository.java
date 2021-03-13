package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.LicensePaymentHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LicensePaymentHistoryRepository extends JpaRepository<LicensePaymentHistoryEntity, String> {
}
