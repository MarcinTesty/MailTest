package com.shootingplace.shootingplace.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.UUID;

@Entity
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

    private Boolean isPaid;



    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public LocalDate getValidThru() {
        return validThru;
    }

    public void setValidThru(LocalDate validThru) {
        this.validThru = validThru;
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

    public Boolean getValid() {
        return isValid;
    }

    public void setValid(Boolean valid) {
        isValid = valid;
    }

    public Boolean getCanProlong() {
        return canProlong;
    }

    public void setCanProlong(Boolean canProlong) {
        this.canProlong = canProlong;
    }


    public Boolean getPaid() {
        return isPaid;
    }

    public void setPaid(Boolean paid) {
        isPaid = paid;
    }
}
