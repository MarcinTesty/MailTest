package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.OtherPersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtherPersonRepository extends JpaRepository<OtherPersonEntity, Integer> {
}
