package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {
}
