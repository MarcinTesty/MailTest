package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AddressRepository extends JpaRepository<AddressEntity, UUID> {
}
