package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.FilesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilesRepository extends JpaRepository<FilesEntity, String> {
}

