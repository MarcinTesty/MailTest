package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<MemberEntity, UUID> {
    Optional<MemberEntity> findByPesel(String pesel);

    Optional<MemberEntity> findByEmail(String email);

    Optional<MemberEntity> findByLegitimationNumber(Integer legitimationNumber);
    Set<MemberEntity> findAllByActive(Boolean active);
    Set<MemberEntity> findAllByActiveAndErased(Boolean active, Boolean erased);
    Set<MemberEntity> findAllByActiveAndAdult(Boolean active,Boolean adult);

    Optional<MemberEntity> findByPhoneNumber(String phoneNumber);

    Optional<MemberEntity> findByIDCard(String IDCard);
    List<MemberEntity> findAllByErased(Boolean erased);
}
