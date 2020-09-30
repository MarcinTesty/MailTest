package com.shootingplace.shootingplace.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LicenseEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID uuid;
    @Pattern(regexp = "[0-9]*")
    private String number;

    private LocalDate validThru;

    private Boolean pistolPermission;
    private Boolean riflePermission;
    private Boolean shotgunPermission;

    private Boolean isValid;

    private Boolean canProlong = false;


    private String club;
}
