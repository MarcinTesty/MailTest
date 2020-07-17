package com.shootingplace.shootingplace.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    private LocalDate joinDate;
    private Integer legitimationNumber;
    @NotBlank
    private String firstName;
    @NotBlank
    private String secondName;
    private License license;
    private ShootingPatent shootingPatent;
    @Email
    private String email;
    private String pesel;
    private String IDCard;
    private String phoneNumber;
    private WeaponPermission weaponPermission;
    private Address address;
    private Contribution contribution;
    private Boolean active = false;
    private Boolean adult = true;
    private Boolean erased = false;
}
