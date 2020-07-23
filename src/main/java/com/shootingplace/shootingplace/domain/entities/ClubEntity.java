package com.shootingplace.shootingplace.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClubEntity {

    @Id
    @GeneratedValue
    private Integer id;

    private String name = "Klub Strzelecki Dziesiątka LOK Łódź";
    private String licenseNumber = "1233/2020";

}
