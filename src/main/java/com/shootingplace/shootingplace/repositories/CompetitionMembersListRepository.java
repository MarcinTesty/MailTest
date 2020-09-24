package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.CompetitionMembersListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompetitionMembersListRepository extends JpaRepository<CompetitionMembersListEntity, UUID> {
}
