package com.shootingplace.shootingplace.domain.entities;

import com.shootingplace.shootingplace.validators.ValidPESEL;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.UUID;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "members")
public class MemberEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID uuid;

    private LocalDate joinDate;
    private Integer legitimationNumber;
    @NotBlank
    private String firstName;
    @NotBlank
    private String secondName;
    private String licenseNumber;
    private String shootingPatentNumber;
    @NotBlank
    @Email
    private String email;
    @ValidPESEL
    @Pattern(regexp = "[0-9]*")
    private String pesel;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private AddressEntity address;
    @NotBlank
    @Pattern(regexp = "^\\+[0-9]{11}$")
    private String phoneNumber;
    private Boolean weaponPermission;
    private Boolean active = false;
}
