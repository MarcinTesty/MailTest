package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.ErasedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErasedRepository extends JpaRepository<ErasedEntity,String> {
}
