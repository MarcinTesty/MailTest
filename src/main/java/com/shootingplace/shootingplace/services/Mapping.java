package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.*;
import com.shootingplace.shootingplace.domain.models.*;
import com.shootingplace.shootingplace.domain.models.WeaponPermission;

import java.util.Optional;

class Mapping {

    static Member map(MemberEntity e) {
        return Member.builder()
                .joinDate(e.getJoinDate())
                .legitimationNumber(e.getLegitimationNumber())
                .firstName(e.getFirstName())
                .secondName(e.getSecondName())
                .shootingPatent(map(e.getShootingPatent()))
                .license(map(e.getLicense()))
                .email(e.getEmail())
                .pesel(e.getPesel())
                .IDCard(e.getIDCard())
                .phoneNumber(e.getPhoneNumber())
                .weaponPermission(map(e.getWeaponPermission()))
                .active(e.getActive())
                .adult(e.getAdult())
                .erased(e.getErased())
                .address(map(e.getAddress()))
                .contribution(map(e.getContribution()))
                .history(map(e.getHistory()))
                .build();
    }

    static MemberEntity map(Member e) {
        return MemberEntity.builder()
                .joinDate(e.getJoinDate())
                .legitimationNumber(e.getLegitimationNumber())
                .firstName(e.getFirstName())
                .secondName(e.getSecondName())
                .shootingPatent(map(e.getShootingPatent()))
                .license(map(e.getLicense()))
                .email(e.getEmail())
                .pesel(e.getPesel())
                .IDCard(e.getIDCard())
                .phoneNumber(e.getPhoneNumber())
                .weaponPermission(map(e.getWeaponPermission()))
                .active(e.getActive())
                .adult(e.getAdult())
                .erased(e.getErased())
                .address(map(e.getAddress()))
                .contribution(map(e.getContribution()))
                .history(map(e.getHistory()))
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
                        .pistolPermission(e.getPistolPermission())
                        .riflePermission(e.getRiflePermission())
                        .shotgunPermission(e.getShotgunPermission())
                        .validThru(e.getValidThru())
                        .isValid(e.getIsValid())
                        .club(e.getClub())
                        .build()).orElse(null);
    }

    static LicenseEntity map(License l) {
        return Optional.ofNullable(l)
                .map(e -> LicenseEntity.builder()
                        .number(e.getNumber())
                        .pistolPermission(e.getPistolPermission())
                        .riflePermission(e.getRiflePermission())
                        .shotgunPermission(e.getShotgunPermission())
                        .validThru(e.getValidThru())
                        .isValid(e.getIsValid())
                        .club(e.getClub())
                        .build()).orElse(null);
    }

    static ShootingPatentEntity map(ShootingPatent s) {
        return Optional.ofNullable(s)
                .map(e -> ShootingPatentEntity.builder()
                        .patentNumber(e.getPatentNumber())
                        .dateOfPosting(e.getDateOfPosting())
                        .pistolPermission(e.getPistolPermission())
                        .riflePermission(e.getRiflePermission())
                        .shotgunPermission(e.getShotgunPermission())
                        .build()).orElse(null);
    }

    static ShootingPatent map(ShootingPatentEntity s) {
        return Optional.ofNullable(s)
                .map(e -> ShootingPatent.builder()
                        .patentNumber(e.getPatentNumber())
                        .dateOfPosting(e.getDateOfPosting())
                        .pistolPermission(e.getPistolPermission())
                        .riflePermission(e.getRiflePermission())
                        .shotgunPermission(e.getShotgunPermission())
                        .build()).orElse(null);
    }

    static ContributionEntity map(Contribution c) {
        return Optional.ofNullable(c).map(e -> ContributionEntity.builder()
                .contribution(e.getContribution())
                .paymentDay(e.getPaymentDay())
                .build()).orElse(null);
    }

    static Contribution map(ContributionEntity c) {
        return Optional.ofNullable(c).map(e -> Contribution.builder()
                .contribution(e.getContribution())
                .paymentDay(e.getPaymentDay())
                .build()).orElse(null);
    }

    static History map(HistoryEntity r) {
        return Optional.ofNullable(r).map(e -> History.builder()
                .contributionRecord(e.getContributionRecord())
                .licenseHistory(e.getLicenseHistory())
                .patentDay(e.getPatentDay())
                .patentFirstRecord(e.getPatentFirstRecord())
                .build()).orElse(null);
    }

    static HistoryEntity map(History r) {
        return Optional.ofNullable(r).map(e -> HistoryEntity.builder()
                .contributionRecord(e.getContributionRecord())
                .licenseHistory(e.getLicenseHistory())
                .patentDay(e.getPatentDay())
                .patentFirstRecord(e.getPatentFirstRecord())
                .build()).orElse(null);
    }

    static ElectronicEvidence map(ElectronicEvidenceEntity el) {
        return Optional.ofNullable(el).map((e -> ElectronicEvidence.builder()
                .date(e.getDate())
                .build())).orElse(null);
    }

    static ElectronicEvidenceEntity map(ElectronicEvidence el) {
        return Optional.ofNullable(el).map((e -> ElectronicEvidenceEntity.builder()
                .date(e.getDate())
                .build())).orElse(null);
    }

    static WeaponPermission map(WeaponPermissionEntity w) {
        return Optional.ofNullable(w).map(e -> WeaponPermission.builder()
                .number(e.getNumber())
                .isExist(e.getIsExist())
                .build()).orElse(null);
    }

    static WeaponPermissionEntity map(WeaponPermission w) {
        return Optional.ofNullable(w).map(e -> WeaponPermissionEntity.builder()
                .number(e.getNumber())
                .isExist(e.getIsExist())
                .build()).orElse(null);
    }
}
