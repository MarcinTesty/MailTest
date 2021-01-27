package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<AddressEntity, String> {
}
