package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
public interface MemberRepository extends JpaRepository<MemberEntity, UUID> {
    Optional<MemberEntity> findByPesel(String pesel);
    Optional<MemberEntity> findByEmail(String email);
    Optional<MemberEntity> findByLegitimationNumber(Integer legitimationNumber);
    Set<MemberEntity> findAllByActive(Boolean b);
    Optional<MemberEntity> findByPhoneNumber(String phoneNumber);
}
