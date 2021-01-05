package com.shootingplace.shootingplace.services;

import com.shootingplace.shootingplace.domain.entities.*;
import com.shootingplace.shootingplace.domain.models.*;
import com.shootingplace.shootingplace.domain.models.WeaponPermission;

import java.util.Optional;

public class Mapping {

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
                .history(map(e.getHistory()))
                .memberPermissions(map(e.getMemberPermissions()))
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
                .history(map(e.getHistory()))
                .memberPermissions(map(e.getMemberPermissions()))
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
                        .isValid(e.getValid())
                        .canProlong(e.getCanProlong())
                        .isPaid(e.getPaid())
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
                        .isValid(e.getValid())
                        .canProlong(e.getCanProlong())
                        .isPaid(e.getPaid())
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
                .validThru(e.getValidThru())
                .paymentDay(e.getPaymentDay())
                .build()).orElse(null);
    }

    static Contribution map(ContributionEntity c) {
        return Optional.ofNullable(c).map(e -> Contribution.builder()
                .validThru(e.getValidThru())
                .paymentDay(e.getPaymentDay())
                .build()).orElse(null);
    }

    static History map(HistoryEntity r) {
        return Optional.ofNullable(r).map(e -> History.builder()
                .licenseHistory(e.getLicenseHistory())
                .patentDay(e.getPatentDay())
                .patentFirstRecord(e.getPatentFirstRecord())
                .licensePaymentHistory(e.getLicensePaymentHistory())
                .pistolCounter(e.getPistolCounter())
                .rifleCounter(e.getRifleCounter())
                .shotgunCounter(e.getShotgunCounter())
                .build()).orElse(null);
    }

    static HistoryEntity map(History r) {
        return Optional.ofNullable(r).map(e -> HistoryEntity.builder()
                .licenseHistory(e.getLicenseHistory())
                .patentDay(e.getPatentDay())
                .patentFirstRecord(e.getPatentFirstRecord())
                .licensePaymentHistory(e.getLicensePaymentHistory())
                .pistolCounter(e.getPistolCounter())
                .rifleCounter(e.getRifleCounter())
                .shotgunCounter(e.getShotgunCounter())
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
                .isExist(e.getExist())
                .build()).orElse(null);
    }

    static WeaponPermissionEntity map(WeaponPermission w) {
        return Optional.ofNullable(w).map(e -> WeaponPermissionEntity.builder()
                .number(e.getNumber())
                .isExist(e.getExist())
                .build()).orElse(null);
    }

    static MemberPermissions map(MemberPermissionsEntity m) {
        return Optional.ofNullable(m).map(e -> MemberPermissions.builder()
                .instructorNumber(e.getInstructorNumber())
                .arbiterNumber(e.getArbiterNumber())
                .arbiterClass(e.getArbiterClass())
                .arbiterPermissionValidThru(e.getArbiterPermissionValidThru())
                .shootingLeaderNumber(e.getShootingLeaderNumber())
                .build()).orElse(null);
    }

    static MemberPermissionsEntity map(MemberPermissions m) {
        return Optional.ofNullable(m).map(e -> MemberPermissionsEntity.builder()
                .instructorNumber(e.getInstructorNumber())
                .arbiterNumber(e.getArbiterNumber())
                .arbiterClass(e.getArbiterClass())
                .arbiterPermissionValidThru(e.getArbiterPermissionValidThru())
                .shootingLeaderNumber(e.getShootingLeaderNumber())
                .build()).orElse(null);
    }

    static Tournament map(TournamentEntity t) {
        return Tournament.builder()
                .name(t.getName())
                .date(t.getDate())
                .open(t.isOpen())
                .build();
    }

    static TournamentEntity map(Tournament t) {
        return TournamentEntity.builder()
                .name(t.getName())
                .date(t.getDate())
                .open(t.getOpen())
                .build();
    }

    static Competition map(CompetitionEntity c) {
        return Competition.builder()
                .name(c.getName())
                .build();
    }

    static CompetitionEntity map(Competition c) {
        return CompetitionEntity.builder()
                .name(c.getName())
                .build();
    }

    public static FilesEntity map(FilesModel f) {
        return Optional.ofNullable(f).map(e -> FilesEntity.builder()
                .name(e.getName())
                .data(e.getData())
                .type(e.getType())
                .build()).orElse(null);
    }

    public static FilesModel map(FilesEntity f) {
        return Optional.ofNullable(f).map(e -> FilesModel.builder()
                .name(e.getName())
                .data(e.getData())
                .type(e.getType())
                .build()).orElse(null);
    }

    public static AmmoEvidenceEntity map(AmmoEvidence a) {
        return Optional.ofNullable(a).map(e -> AmmoEvidenceEntity.builder()
                .date(e.getDate())
                .build()).orElse(null);
    }

    public static AmmoEvidence map(AmmoEvidenceEntity a) {
        return Optional.ofNullable(a).map(e -> AmmoEvidence.builder()
                .date(e.getDate())
                .build()).orElse(null);
    }

    public static Caliber map(CaliberEntity c) {
        return Optional.ofNullable(c).map(e -> Caliber.builder()
                .name(e.getName())
                .quantity(e.getQuantity())
                .ammoUsed(e.getAmmoUsed())
                .build()).orElse(null);
    }

    public static CaliberEntity map(Caliber c) {
        return Optional.ofNullable(c).map(e -> CaliberEntity.builder()
                .name(e.getName())
                .quantity(e.getQuantity())
                .ammoUsed(e.getAmmoUsed())
                .build()).orElse(null);
    }

    public static PersonalEvidenceEntity map(PersonalEvidence p) {
        return Optional.ofNullable(p).map(e -> PersonalEvidenceEntity.builder()
                .build()).orElse(null);
    }

    public static PersonalEvidence map(PersonalEvidenceEntity p) {
        return Optional.ofNullable(p).map(e -> PersonalEvidence.builder()
                .build()).orElse(null);
    }

    public static CompetitionMembersList map(CompetitionMembersListEntity c) {
        return Optional.ofNullable(c).map(e -> CompetitionMembersList.builder()
                .name(e.getName())
                .build()).orElse(null);
    }

    public static CompetitionMembersListEntity map(CompetitionMembersList c) {
        return Optional.ofNullable(c).map(e -> CompetitionMembersListEntity.builder()
                .name(e.getName())
                .build()).orElse(null);
    }

    public static AmmoUsedToEvidenceEntity map(AmmoUsedEvidence a) {
        return Optional.ofNullable(a).map(e -> AmmoUsedToEvidenceEntity.builder()
                .caliberName(a.getCaliberName())
                .caliberUUID(a.getCaliberUUID())
                .counter(a.getCounter())
                .memberEntity(a.getMemberEntity())
                .otherPersonEntity(a.getOtherPersonEntity())
                .name(a.getUserName())
                .build()).orElse(null);
    }

    public static AmmoUsedEntity map(AmmoUsedPersonal a) {
        return Optional.ofNullable(a).map(e -> AmmoUsedEntity.builder()
                .caliberName(a.getCaliberName())
                .counter(a.getCounter())
                .memberUUID(a.getMemberUUID())
                .caliberUUID(a.getCaliberUUID())
                .build()).orElse(null);

    }

    static AmmoDTO map1(AmmoEvidenceEntity a) {
        return AmmoDTO.builder()
                .evidenceUUID(a.getUuid())
                .date(a.getDate())
                .build();

    }

    static TournamentDTO map1(TournamentEntity a) {
        return TournamentDTO.builder()
                .tournamentUUID(a.getUuid())
                .date(a.getDate())
                .name(a.getName())
                .build();

    }
}
