package com.shootingplace.shootingplace.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShootingPatentEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID uuid;
    private String patentNumber;

    private Boolean pistolPermission;

    private Boolean riflePermission;

    private Boolean shotgunPermission;

    private LocalDate dateOfPosting;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getPatentNumber() {
        return patentNumber;
    }

    public void setPatentNumber(String patentNumber) {
        this.patentNumber = patentNumber;
    }

    public Boolean getPistolPermission() {
        return pistolPermission;
    }

    public void setPistolPermission(Boolean pistolPermission) {
        this.pistolPermission = pistolPermission;
    }

    public Boolean getRiflePermission() {
        return riflePermission;
    }

    public void setRiflePermission(Boolean riflePermission) {
        this.riflePermission = riflePermission;
    }

    public Boolean getShotgunPermission() {
        return shotgunPermission;
    }

    public void setShotgunPermission(Boolean shotgunPermission) {
        this.shotgunPermission = shotgunPermission;
    }

    public LocalDate getDateOfPosting() {
        return dateOfPosting;
    }

    public void setDateOfPosting(LocalDate dateOfPosting) {
        this.dateOfPosting = dateOfPosting;
    }
}
