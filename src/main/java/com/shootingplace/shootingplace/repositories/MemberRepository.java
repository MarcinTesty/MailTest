package com.shootingplace.shootingplace.repositories;

import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MemberRepository extends JpaRepository<MemberEntity, String> {
    Optional<MemberEntity> findByPesel(String pesel);

    Optional<MemberEntity> findByEmail(String email);

    Optional<MemberEntity> findByLegitimationNumber(Integer legitimationNumber);

    Set<MemberEntity> findAllByActive(Boolean active);

    Set<MemberEntity> findAllByActiveAndAdultAndErased(Boolean active, Boolean adult, Boolean erase);

    Optional<MemberEntity> findByPhoneNumber(String phoneNumber);

    Optional<MemberEntity> findByIDCard(String IDCard);

    List<MemberEntity> findAllByErasedIsTrue();

    List<MemberEntity> findAllByFirstNameOrSecondName(String firstName,String secondName);

}
