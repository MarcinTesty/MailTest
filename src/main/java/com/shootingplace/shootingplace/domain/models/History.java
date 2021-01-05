package com.shootingplace.shootingplace.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Builder
@NoArgsConstructor
@AllArgsConstructor
public class History {
    private String[] licenseHistory;
    private LocalDate[] licensePaymentHistory;

    private Boolean patentFirstRecord = false;
    private LocalDate[] patentDay;

    private Integer pistolCounter = 0;
    private Integer rifleCounter = 0;
    private Integer shotgunCounter = 0;

    public String[] getLicenseHistory() {
        return licenseHistory;
    }

    public void setLicenseHistory(String[] licenseHistory) {
        this.licenseHistory = licenseHistory;
    }

    public LocalDate[] getLicensePaymentHistory() {
        return licensePaymentHistory;
    }

    public void setLicensePaymentHistory(LocalDate[] licensePaymentHistory) {
        this.licensePaymentHistory = licensePaymentHistory;
    }

    public Boolean getPatentFirstRecord() {
        return patentFirstRecord;
    }

    public void setPatentFirstRecord(Boolean patentFirstRecord) {
        this.patentFirstRecord = patentFirstRecord;
    }

    public LocalDate[] getPatentDay() {
        return patentDay;
    }

    public void setPatentDay(LocalDate[] patentDay) {
        this.patentDay = patentDay;
    }

    public Integer getPistolCounter() {
        return pistolCounter;
    }

    public void setPistolCounter(Integer pistolCounter) {
        this.pistolCounter = pistolCounter;
    }

    public Integer getRifleCounter() {
        return rifleCounter;
    }

    public void setRifleCounter(Integer rifleCounter) {
        this.rifleCounter = rifleCounter;
    }

    public Integer getShotgunCounter() {
        return shotgunCounter;
    }

    public void setShotgunCounter(Integer shotgunCounter) {
        this.shotgunCounter = shotgunCounter;
    }
}
