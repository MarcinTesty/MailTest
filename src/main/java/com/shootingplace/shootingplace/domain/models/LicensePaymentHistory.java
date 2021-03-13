package com.shootingplace.shootingplace.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LicensePaymentHistory {

    private String uuid;

    private LocalDate date;

    private String memberUUID;

    private Integer validForYear;

    public String getUuid() {
        return uuid;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getMemberUUID() {
        return memberUUID;
    }

    public void setMemberUUID(String memberUUID) {
        this.memberUUID = memberUUID;
    }

    public Integer getValidForYear() {
        return validForYear;
    }

    public void setValidForYear(Integer validForYear) {
        this.validForYear = validForYear;
    }
}
