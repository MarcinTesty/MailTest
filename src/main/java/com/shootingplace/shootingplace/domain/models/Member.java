package com.shootingplace.shootingplace.domain.models;

import com.shootingplace.shootingplace.validators.ValidPESEL;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.UniqueElements;
import org.hibernate.validator.constraints.pl.PESEL;

import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Date;

import static javax.persistence.GenerationType.SEQUENCE;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    private LocalDate joinDate;
    private Integer legitimationNumber;
    private String firstName;
    private String secondName;
    private String licenseNumber;
    private String shootingPatentNumber;
    @NotBlank
    @Email(message = "e-mail nie może być pusty ani zawierać spacji")
    private String email;
    @ValidPESEL
    @Pattern(regexp = "[0-9]*")
    private String pesel;
    private String address;
}
