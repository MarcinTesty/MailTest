package com.shootingplace.shootingplace.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    private LocalDate joinDate;
    private Integer legitimationNumber;
    private String firstName;
    private String secondName;
    private License license;
    private ShootingPatent shootingPatent;
    private String email = "";

    private String pesel;
    private String IDCard;
    private Address address;
    private String phoneNumber;
    private WeaponPermission weaponPermission;

    private Boolean active = true;
    private Boolean adult = true;
    private Boolean erased = false;

    private History history;

    private MemberPermissions memberPermissions;

    private PersonalEvidence personalEvidence;

    private FilesModel contributionFile;

    private FilesModel personalCardFile;


}
