package com.shootingplace.shootingplace.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AmmoInEvidenceEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID uuid;

    private String caliberName;

    private UUID caliberUUID;

    private UUID evidenceUUID;

    private Integer quantity;
    @ManyToMany
    private List<AmmoUsedToEvidenceEntity> ammoUsedToEvidenceEntityList;

    public UUID getUuid() {
        return uuid;
    }

    public String getCaliberName() {
        return caliberName;
    }

    public void setCaliberName(String caliberName) {
        this.caliberName = caliberName;
    }

    public UUID getCaliberUUID() {
        return caliberUUID;
    }

    public void setCaliberUUID(UUID caliberUUID) {
        this.caliberUUID = caliberUUID;
    }

    public UUID getEvidenceUUID() {
        return evidenceUUID;
    }

    public void setEvidenceUUID(UUID evidenceUUID) {
        this.evidenceUUID = evidenceUUID;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public List<AmmoUsedToEvidenceEntity> getAmmoUsedToEvidenceEntityList() {
        return ammoUsedToEvidenceEntityList;
    }

    public void setAmmoUsedToEvidenceEntityList(List<AmmoUsedToEvidenceEntity> ammoUsedToEvidenceEntityList) {
        this.ammoUsedToEvidenceEntityList = ammoUsedToEvidenceEntityList;
    }
}
