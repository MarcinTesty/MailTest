package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.MemberPermissionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberPermissionsRepository extends JpaRepository<MemberPermissionsEntity, String> {
}
