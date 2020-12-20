package com.shootingplace.shootingplace.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AmmoUsed {
    private String caliberName;

    private UUID member;

    private Integer counter;

    private UUID caliberUUID;

    public UUID getCaliberUUID() {
        return caliberUUID;
    }

    public void setCaliberUUID(UUID caliberUUID) {
        this.caliberUUID = caliberUUID;
    }

    public String getCaliberName() {
        return caliberName;
    }

    public void setCaliberName(String caliberName) {
        this.caliberName = caliberName;
    }

    public UUID getMember() {
        return member;
    }

    public void setMember(UUID member) {
        this.member = member;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }
}
