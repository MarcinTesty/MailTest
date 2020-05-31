package com.shootingplace.shootingplace.domain.models;

import com.shootingplace.shootingplace.validators.ValidPESEL;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
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
    private String licenseNumber;
    private String shootingPatentNumber;
    @NotBlank
    @Email(message = "e-mail nie może być pusty ani zawierać spacji")
    private String email;
//    @ValidPESEL
    private String pesel;
    private String phoneNumber;
    private Boolean weaponPermission;
    private String address;
    private Boolean active = false;
}
