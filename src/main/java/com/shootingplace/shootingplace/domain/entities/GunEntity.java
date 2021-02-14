package com.shootingplace.shootingplace.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GunEntity {

    @Id
    @GeneratedValue(generator = "id")
    @GenericGenerator(name = "id", strategy = "org.hibernate.id.UUIDGenerator")
    private String uuid;

    private String modelName;

    private String caliber;

    private String serialNumber;

    private String productionYear;

    private String numberOfMagazines;

    private String gunCertificateSerialNumber;

    private String additionalEquipment;

    private String basisForPurchaseOrAssignment;

    private String comment;

    private boolean inStock;
}
