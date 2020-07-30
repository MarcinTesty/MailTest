package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.MemberPermissionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MemberPermissionsRepository extends JpaRepository<MemberPermissionsEntity, UUID> {
}
