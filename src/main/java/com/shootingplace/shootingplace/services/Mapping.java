package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.AddressEntity;
import com.shootingplace.shootingplace.domain.entities.LicenseEntity;
import com.shootingplace.shootingplace.domain.entities.MemberEntity;
import com.shootingplace.shootingplace.domain.models.Address;
import com.shootingplace.shootingplace.domain.models.License;
import com.shootingplace.shootingplace.domain.models.Member;

import java.util.Optional;

class Mapping {

    static Member map(MemberEntity e) {
        return Member.builder()
                .joinDate(e.getJoinDate())
                .legitimationNumber(e.getLegitimationNumber())
                .firstName(e.getFirstName())
                .secondName(e.getSecondName())
                .shootingPatentNumber(e.getShootingPatentNumber())
                .license(map(e.getLicense()))
                .email(e.getEmail())
                .pesel(e.getPesel())
                .phoneNumber(e.getPhoneNumber())
                .weaponPermission(e.getWeaponPermission())
                .active(e.getActive())
                .address(map(e.getAddress()))
                .build();
    }

    static MemberEntity map(Member e) {
        return MemberEntity.builder()
                .joinDate(e.getJoinDate())
                .legitimationNumber(e.getLegitimationNumber())
                .firstName(e.getFirstName())
                .secondName(e.getSecondName())
                .shootingPatentNumber(e.getShootingPatentNumber())
                .license(map(e.getLicense()))
                .email(e.getEmail())
                .pesel(e.getPesel())
                .phoneNumber(e.getPhoneNumber())
                .weaponPermission(e.getWeaponPermission())
                .active(e.getActive())
                .address(map(e.getAddress()))
                .build();
    }

    static Address map(AddressEntity a) {
        return Optional.ofNullable(a)
                .map(e -> Address.builder()
                        .zipCode(e.getZipCode())
                        .postOfficeCity(e.getPostOfficeCity())
                        .street(e.getStreet())
                        .streetNumber(e.getStreetNumber())
                        .flatNumber(e.getFlatNumber())
                        .build()).orElse(null);

    }

    static AddressEntity map(Address a) {
        return Optional.ofNullable(a)
                .map(e -> AddressEntity.builder()
                        .zipCode(e.getZipCode())
                        .postOfficeCity(e.getPostOfficeCity())
                        .street(e.getStreet())
                        .streetNumber(e.getStreetNumber())
                        .flatNumber(e.getFlatNumber())
                        .build()).orElse(null);
    }

    static License map(LicenseEntity l) {
        return Optional.ofNullable(l)
                .map(e -> License.builder()
                        .number(e.getNumber())
                        .disciplines(e.getDisciplines())
                        .validThrough(e.getValidThrough())
                        .club(e.getClub())
                        .build()).orElse(null);
    }
    static LicenseEntity map(License l){
        return Optional.ofNullable(l)
                .map(e -> LicenseEntity.builder()
                .number(e.getNumber())
                .disciplines(e.getDisciplines())
                .validThrough(e.getValidThrough())
                .club(e.getClub())
                .build()).orElse(null);
    }

}
