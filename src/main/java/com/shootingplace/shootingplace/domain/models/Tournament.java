package com.shootingplace.shootingplace.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Tournament {

    private String name;
    private LocalDate date;

    private Member commissionRTSArbiter;
    private Member mainArbiter;
    private Boolean open;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Member getCommissionRTSArbiter() {
        return commissionRTSArbiter;
    }

    public void setCommissionRTSArbiter(Member commissionRTSArbiter) {
        this.commissionRTSArbiter = commissionRTSArbiter;
    }

    public Member getMainArbiter() {
        return mainArbiter;
    }

    public void setMainArbiter(Member mainArbiter) {
        this.mainArbiter = mainArbiter;
    }

    public Boolean getOpen() {
        return open;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }
}
