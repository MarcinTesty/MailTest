package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.CompetitionMembersListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitionMembersListRepository extends JpaRepository<CompetitionMembersListEntity, String> {
}
