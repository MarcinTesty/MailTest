package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.FilesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FilesRepository extends JpaRepository<FilesEntity, UUID> {
}

