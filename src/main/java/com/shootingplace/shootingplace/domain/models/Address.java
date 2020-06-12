package com.shootingplace.shootingplace.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address {

    private String zipCode;
    private String postOfficeCity;
    private String street;
    private String streetNumber;
    private String flatNumber;
}
