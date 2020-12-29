package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.OtherPersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OtherPersonRepository extends JpaRepository<OtherPersonEntity, UUID> {
}
