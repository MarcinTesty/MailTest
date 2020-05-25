package com.shootingplace.shootingplace.domain.models;

import com.shootingplace.shootingplace.validators.ValidPESEL;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.pl.PESEL;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    private String firstName;
    private String secondName;
    private String licenseNumber;
    @NotBlank
    @Email(message = "e-mail nie może być pusty ani zawierać spacji")
    private String email;
    @ValidPESEL
    private String pesel;
    private String address;
}
