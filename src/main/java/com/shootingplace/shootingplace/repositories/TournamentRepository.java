package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.TournamentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentRepository extends JpaRepository<TournamentEntity, String> {
}
