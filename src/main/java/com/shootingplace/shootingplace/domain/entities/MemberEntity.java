package com.shootingplace.shootingplace.domain.entities;

import com.shootingplace.shootingplace.validators.ValidPESEL;
import lombok.*;
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
    @OneToOne(orphanRemoval = true)
    private LicenseEntity license;
    @OneToOne(orphanRemoval = true)
    private ShootingPatentEntity shootingPatent;
    @Email
    private String email = "";
    @NotBlank
    @ValidPESEL
    @Pattern(regexp = "[0-9]*")
    private String pesel;
    @NotBlank
    @Pattern(regexp = "\\w{3}\\d{6}")
    private String IDCard;

    @OneToOne
    private AddressEntity address;
    @NotBlank
    @Pattern(regexp = "^\\+[0-9]{11}$")
    private String phoneNumber;
    @OneToOne(orphanRemoval = true)
    private WeaponPermissionEntity weaponPermission;
    @OneToOne(orphanRemoval = true)
    private ContributionEntity contribution;
    private Boolean active = false;
    private Boolean adult = true;
    private Boolean erased = false;

    @OneToOne(orphanRemoval = true)
    private HistoryEntity history;

    @OneToOne(orphanRemoval = true)
    private MemberPermissionsEntity memberPermissions;
}
