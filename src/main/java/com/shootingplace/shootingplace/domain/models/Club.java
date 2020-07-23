package com.shootingplace.shootingplace.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Club {

    private String name = "Klub Strzelecki Dziesiątka LOK Łódź";
    private String licenseNumber = "1233/2020";
}
