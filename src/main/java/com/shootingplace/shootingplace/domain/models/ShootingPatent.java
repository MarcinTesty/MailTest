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
public class ShootingPatent {


    private String patentNumber;

    private Boolean pistolPermission;

    private Boolean riflePermission;

    private Boolean shotgunPermission;

    private LocalDate dateOfPosting;
}
