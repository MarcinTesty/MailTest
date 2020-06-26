package com.shootingplace.shootingplace.domain.entities;

import com.shootingplace.shootingplace.domain.enums.Disciplines;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    private String number;

    private LocalDate validThru;

    private Boolean pistolPermission;
    private Boolean riflePermission;
    private Boolean shotgunPermission;


    private String club;
}
