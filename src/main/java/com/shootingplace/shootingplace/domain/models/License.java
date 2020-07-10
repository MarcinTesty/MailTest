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
public class License {


    private String number;

    private LocalDate validThru;

    private Boolean pistolPermission;
    private Boolean riflePermission;
    private Boolean shotgunPermission;

    private String club;
}
